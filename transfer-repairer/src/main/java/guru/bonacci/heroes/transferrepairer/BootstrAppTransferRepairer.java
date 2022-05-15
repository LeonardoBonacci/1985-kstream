package guru.bonacci.heroes.transferrepairer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;
import static guru.bonacci.heroes.kafka.Constants.MAX_TRANSFER_PROCESSING_TIME_SEC;

import java.math.BigDecimal;
import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
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
public class BootstrAppTransferRepairer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferRepairer.class, args);
	}

  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> eventualStream = 
     builder
      .stream(TRANSFER_EVENTUAL_TOPIC, Consumed.with(Serdes.String(), transferSerde))
      .peek((k,v) -> log.info("in {}<>{}", k, v));

    eventualStream
      .leftJoin(eventualStream, (t1, t2) -> new TransferPair(t1, t2), // order unknown 
        JoinWindows.of(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)).before(Duration.ofMillis(0)),
        StreamJoined.with(Serdes.String(), transferSerde, transferSerde)
      )
      .peek((k,v) -> log.info("joined {}<>{}", k, v))
      .filter((transferId, pair) -> theOne(pair))
      .peek((k,v) -> log.debug("filtered {}<>{}", k, v))
      .mapValues((transferId, pair) -> pair.getT1())
      .peek((k,v) -> log.info("out {}<>{}", k, v))
      .to(TRANSFER_CONSISTENT_TOPIC, Produced.with(Serdes.String(), transferSerde));
    
    return eventualStream;
  }
  
  /**
   * Arbitrarily, we're taking the positive left and the negative right
   * This excludes 4 out of 5.
   * 
   * Example for amount 10
   * 10 and null
   * 10 and 10
   * -10 and 10
   * 10 and -10
   * -10 and -10
   * 
   * null and 10 is not joined
   */
  private boolean theOne(TransferPair pair) {
   if (pair.getT1() == null || pair.getT2() == null) { // left(-join) over
     return false;
   }

   return pair.getT1().getAmount().compareTo(BigDecimal.ZERO) > -1 &&
       pair.getT2().getAmount().compareTo(BigDecimal.ZERO) < 1;
  }
}
