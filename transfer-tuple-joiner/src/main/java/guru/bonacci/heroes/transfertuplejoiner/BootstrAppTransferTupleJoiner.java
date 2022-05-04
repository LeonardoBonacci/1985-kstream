package guru.bonacci.heroes.transfertuplejoiner;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import guru.bonacci.kafka.serialization.JacksonSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppTransferTupleJoiner {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferTupleJoiner.class, args);
	}

	
	@Bean
	public KStream<String, Transfer> tuple(StreamsBuilder builder) {
	  KStream<String, Transfer> stream = // keyed on transferId
	      builder.stream(KafkaTopicNames.TRANSFERS_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), JacksonSerde.of(Transfer.class)));

	  
    stream.peek((k,v) -> log.info(">>> " + k + " <> " + v));
    KStream<Windowed<String>, Aggregation> windowed = stream
	    .groupByKey()
	    .windowedBy(
	        SessionWindows.ofInactivityGapWithNoGrace(Duration.ofMinutes(1)))
	    .aggregate(                                                   
	        Aggregation::new,
	        (transferId, transfer, aggregation) -> aggregation.updateFrom(transfer),
	        (transferId, aggOne, aggTwo) -> new Aggregation(aggOne, aggTwo)
	     )
	    .toStream();
    
    windowed
	    .filter((transferId, aggr) -> aggr.isPaired()) // only pass when tuple is paired
      .mapValues(Aggregation::getTransfer) 
      .to(KafkaTopicNames.TRANSFERS_CONSISTENT_TOPIC);
  	return stream;
	}
}
