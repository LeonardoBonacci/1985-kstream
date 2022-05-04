package guru.bonacci.heroes.transfertupler;

import static guru.bonacci.heroes.domain.Account.identifier;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFERS_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_TUPLES_TOPIC;
import static java.util.Arrays.asList;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppTransferTupler {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferTupler.class, args);
	}

	
	@Bean
	public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);

	  KStream<String, Transfer> stream = 
      builder.stream(TRANSFERS_TOPIC, Consumed.with(Serdes.String(), transferSerde))
      .peek((k,v) -> {
        log.info("------------------");
        log.info("{} <> {}", k, v); 
      });
	  
	  KStream<String, Transfer> rekeyed = 
      stream.flatMap((key, value) -> 
          asList(KeyValue.pair(identifier(value.getPoolId(), value.getFrom()), value), 
                 KeyValue.pair(identifier(value.getPoolId(), value.getTo()), value)));

	  rekeyed
      .peek((k,v) -> log.info("{} <> {}", k, v))
	    .to(TRANSFER_TUPLES_TOPIC, Produced.with(Serdes.String(), transferSerde));
  	return stream;
	}
}