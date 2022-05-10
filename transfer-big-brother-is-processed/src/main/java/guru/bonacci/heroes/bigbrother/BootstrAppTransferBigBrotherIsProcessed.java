package guru.bonacci.heroes.bigbrother;

import static guru.bonacci.heroes.kafka.Constants.MAX_TRANSFER_PROCESSING_TIME_SEC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_HOUSTON_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_TOPIC;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

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
import org.apache.kafka.streams.kstream.Suppressed;
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
public class BootstrAppTransferBigBrotherIsProcessed {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferBigBrotherIsProcessed.class, args);
	}
	 
	
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> transferStream = // keyed on poolId.accountId
      builder // rekey to transferId
        .stream(TRANSFER_TOPIC, Consumed.with(Serdes.String(), transferSerde))
        .peek((k,v) -> log.info("transfer in {}<>{}", k, v))
        .selectKey((poolAccountId, transfer) -> transfer.getTransferId());

    
    KStream<String, Transfer> consistentStream = // keyed on transferId
        builder
          .stream(TRANSFER_CONSISTENT_TOPIC, Consumed.with(Serdes.String(), transferSerde))
          .peek((k,v) -> log.info("consistent in {}<>{}", k, v));

    
    KStream<String, String> joinedStream = // first false, then true
      transferStream
        .leftJoin(consistentStream, (t1, t2) -> String.valueOf(t2 != null),
          JoinWindows.of(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)).before(Duration.ofMillis(0)),
          StreamJoined.with(Serdes.String(), transferSerde, transferSerde))
        .peek((k,v) -> log.info("joined in {}<>{}", k, v));

    
    KStream<Windowed<String>, String> windowed = // value is Boolean
      joinedStream
        .groupByKey()
        .windowedBy(
           SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)))
        .reduce(
            (aggr, curr) -> String.valueOf(Boolean.valueOf(aggr) || Boolean.valueOf(curr)),
            Materialized.with(Serdes.String(), Serdes.String())) // why is there no Serdes.Boolean()??
        .suppress(Suppressed.untilWindowCloses(unbounded()))
        .toStream()
        .peek((k,v) -> log.info("windowed {}<>{}", k, v));

    
    windowed // unwindow
      .map((windowedKey, isPaired) ->  KeyValue.pair(windowedKey.key(), isPaired))
      .filterNot((transferId, isPaired) -> Boolean.valueOf(isPaired)) // (not)processed
      .mapValues((transferId, isPaired) -> 0L)
      .peek((k,v) -> log.info("out {}<>{}", k, v))
      .to(TRANSFER_HOUSTON_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

    //TODO value 0L is serialized to null??
    return transferStream;
  }
}
