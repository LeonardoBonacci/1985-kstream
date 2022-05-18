package guru.bonacci.heroes.bigbrother;

import static guru.bonacci.heroes.kafka.Constants.MAX_TRANSFER_PROCESSING_TIME_SEC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_HOUSTON_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PROCESSED_TOPIC;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
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
public class AppBigBrotherTransferIsPair {

	public static void main(String[] args) {
		SpringApplication.run(AppBigBrotherTransferIsPair.class, args);
	}
	 
  @Bean
  public KStream<String, Long> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);

    KStream<String, Long> processedStream = // keyed on poolAccountId
        builder // rekey to transferId
          .stream(TRANSFER_PROCESSED_TOPIC, Consumed.with(Serdes.String(), transferSerde))
          .peek((transferId, transfer) -> log.info("in {}<>{}", transferId, transfer))
          .map((poolAccountId, transfer) -> KeyValue.pair(transfer.getTransferId(), 1l));

    KStream<Windowed<String>, Long> windowed = 
      processedStream
        .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
        .windowedBy(
           SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)))
        .reduce(
            (aggr, curr) -> aggr + curr, // count -> https://issues.apache.org/jira/browse/KAFKA-9259
            Materialized.with(Serdes.String(), Serdes.Long()))
        .suppress(Suppressed.untilWindowCloses(unbounded()))
        .toStream()
        .peek((transferId, isProcessed) -> log.info("windowed {}<>{}", transferId, isProcessed));

    windowed // unwindow
      .map((windowedKey, counter) ->  KeyValue.pair(windowedKey.key(), counter))
      .filter((transferId, counter) -> counter != 2) // not a pair is alarm
      .peek((transferId, counter) -> log.info("out {}<>{}", transferId, counter))
      .to(TRANSFER_HOUSTON_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
      
    return processedStream;
  }
}
