package guru.bonacci.heroes.transfertuplejoiner;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;

import java.time.Duration;

import org.apache.kafka.clients.admin.NewTopic;
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
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class BootstrAppTransferTupleJoiner {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferTupleJoiner.class, args);
	}

  @Bean
  public NewTopic transferEventual() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC)
      .partitions(1)
      .build();
  }

  @Bean
  public NewTopic transferConsistent() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC)
      .partitions(1)
      .build();
  }
  
	@Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder, @Value("${max.time.difference.sec:42}") Long sessionDuration) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    final var transferCounterSerde = new JsonSerde<TransferCounter>(TransferCounter.class);
  
    KStream<String, Transfer> transferStream = // keyed on transferId
        builder.stream(TRANSFER_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), transferSerde));

    KStream<Windowed<String>, TransferCounter> windowed = 
      transferStream
      .peek((k,v) -> log.info("incoming {}<>{}", k, v))
      .mapValues(TransferCounter::from)
      .groupByKey()
      .windowedBy(
          SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(sessionDuration)))
      .reduce(
          (aggrTransfer, currTransfer) -> new TransferCounter(aggrTransfer, currTransfer),
          Materialized.with(Serdes.String(), transferCounterSerde))
      .toStream();

    windowed
      .filter((transferId, aggrTransfer) -> aggrTransfer != null && aggrTransfer.getCounter() >= 2) // we deal with >2 case elsewhere
      .map((windowedKey, aggr) ->  KeyValue.pair(windowedKey.key(), aggr.getTransfer())) // unwindow
      .to(TRANSFER_CONSISTENT_TOPIC, Produced.with(Serdes.String(), transferSerde));
    return transferStream;
  }
}
