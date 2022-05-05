package guru.bonacci.heroes.transfertuplejoiner;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class BootstrAppTransferTupleJoiner {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferTupleJoiner.class, args);
	}


	@Bean
	public KStream<String, Transfer> topology(StreamsBuilder builder, @Value("${max.time.difference.sec:42}") Long sessionDuration) {
	  final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    final var transferTupleSerde = new JsonSerde<TransferTuple>(TransferTuple.class);
	
	  KStream<String, Transfer> stream = // keyed on transferId
	      builder.stream(TRANSFER_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), transferSerde));

	  KStream<Windowed<String>, TransferTuple> windowed = 
	      stream
	    .peek((k,v) -> log.info("incoming {}<>{}", k, v))
	    .groupByKey()
	    .windowedBy(
	        SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(sessionDuration)))
	    .aggregate(                                                   
	        TransferTuple::new,
	        (transferId, transfer, aggregation) -> aggregation.updateFrom(transfer),
	        (transferId, aggOne, aggTwo) -> new TransferTuple(aggOne, aggTwo),
	        Materialized.with(Serdes.String(), transferTupleSerde)
	     )
	    .toStream();

    windowed
	    .filter((transferId, aggr) -> aggr != null ? aggr.isPaired() : false) // only pass when tuple is paired
      .map((windowedKey, aggr) ->  KeyValue.pair(windowedKey.key(), aggr.getTransfer()))
      .to(TRANSFER_CONSISTENT_TOPIC, Produced.with(Serdes.String(), transferSerde));
  	return stream;
	}
}
