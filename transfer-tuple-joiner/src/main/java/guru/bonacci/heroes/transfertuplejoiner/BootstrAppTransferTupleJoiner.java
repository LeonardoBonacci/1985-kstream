package guru.bonacci.heroes.transfertuplejoiner;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
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
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> eventualStream = 
     builder
      .stream(TRANSFER_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), transferSerde))
      .peek((k,v) -> log.info("incoming {}<>{}", k, v));

    ValueJoiner<Transfer, Transfer, TransferTuple> transferJoiner = (first, second) -> {
      return new TransferTuple(first, second);
    };
  
    eventualStream
      .leftJoin(eventualStream,
        transferJoiner,
        JoinWindows.of(Duration.ofSeconds(42)).before(Duration.ofMillis(-1)),
        StreamJoined.with(Serdes.String(), transferSerde, transferSerde)
        )
      .peek((k,v) -> log.info("joined {}<>{}", k, v))
      .filter((transferId, tuple) -> tuple.getT2() != null) // left join
      .mapValues((transferId, tuple) -> tuple.getT1())
      .to(TRANSFER_CONSISTENT_TOPIC, Produced.with(Serdes.String(), transferSerde));
    
    return eventualStream;
  }
}
