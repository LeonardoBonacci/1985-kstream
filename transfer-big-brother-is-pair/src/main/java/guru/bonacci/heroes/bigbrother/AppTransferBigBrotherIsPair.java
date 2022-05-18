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
import org.apache.kafka.streams.kstream.KStream;
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
public class AppTransferBigBrotherIsPair {

	public static void main(String[] args) {
		SpringApplication.run(AppTransferBigBrotherIsPair.class, args);
	}
	 
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);

    KStream<String, Transfer> processedStream = // keyed on poolAccountId
        builder
          .stream(TRANSFER_PROCESSED_TOPIC, Consumed.with(Serdes.String(), transferSerde))
          .selectKey((poolAccountId, transfer) -> transfer.getTransferId())
          .peek((transferId, transfer) -> log.info("in {}<>{}", transferId, transfer));

    KStream<Windowed<String>, Long> windowed = 
      processedStream
        .groupByKey()
        .windowedBy(
           SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)))
        .count()
        .suppress(Suppressed.untilWindowCloses(unbounded()))
        .toStream()
        .peek((transferId, counter) -> log.info("windowed {}<>{}", transferId, counter));
     
    windowed // unwindow
      .map((windowedKey, counter) ->  KeyValue.pair(windowedKey.key(), counter))
      .filterNot((transferId, counter) -> counter == 2) // pair
      .peek((transferId, counter) -> log.info("out {}<>{}", transferId, counter))
      .to(TRANSFER_HOUSTON_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

    return processedStream;
  }
}
