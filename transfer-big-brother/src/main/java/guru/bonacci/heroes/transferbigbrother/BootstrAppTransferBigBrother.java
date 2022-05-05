package guru.bonacci.heroes.transferbigbrother;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_HOUSTON_TOPIC;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.Windowed;
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
public class BootstrAppTransferBigBrother {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferBigBrother.class, args);
	}

   @Bean
   public KStream<String, Transfer> topology(StreamsBuilder builder) {
     final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
     final var transferTupleCounterSerde = new JsonSerde<TransferTupleCounter>(TransferTupleCounter.class);
    
     KStream<String, Transfer> eventualStream = // keyed on transferId
         builder.stream(TRANSFER_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), transferSerde))
         .peek((k,v) -> log.info("ev {}", v));
  
     KStream<String, Transfer> consistentStream = // keyed on transferId
           builder.stream(TRANSFER_CONSISTENT_TOPIC, Consumed.with(Serdes.String(), transferSerde))
          .peek((k,v) -> log.info("cons {}", v));
      
     KStream<String, TransferTupleCounter> joined = 
       eventualStream.leftJoin(consistentStream,
           (eventual, consistent) -> new TransferTuple(eventual, consistent),
           JoinWindows.of(Duration.ofSeconds(42)), //TODO 
           StreamJoined.with(Serdes.String(), transferSerde, transferSerde)
       )
       .peek((k,v) -> log.info("joined {}<>{}", k, v))
       .mapValues(TransferTupleCounter::from);
     

     KStream<Windowed<String>, TransferTupleCounter> windowed = 
      joined
       .groupByKey()
       .windowedBy(
           SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(42)))
       .reduce(
           (aggrTransferTuple, currTransferTuple) -> new TransferTupleCounter(aggrTransferTuple, currTransferTuple),
           Materialized.with(Serdes.String(), transferTupleCounterSerde))
       .toStream()
       .peek((k,v) -> log.info("windowed {}<>{}", k, v));
       
     windowed
       .filter((transferId,transferTupleCounter) -> transferTupleCounter.getTransferTuple().getTRight() == null)
       .map((windowedKey, transferTupleCounter) ->  KeyValue.pair(windowedKey.key(), transferTupleCounter.getTransferTuple().getTLeft())) // unwindow
       .to(TRANSFER_HOUSTON_TOPIC, Produced.with(Serdes.String(), transferSerde));

     return eventualStream;
   }
}
