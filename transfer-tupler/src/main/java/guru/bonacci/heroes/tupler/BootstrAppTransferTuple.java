package guru.bonacci.heroes.tupler;

import static guru.bonacci.heroes.domain.Transfer.identifier;
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

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import guru.bonacci.kafka.serialization.JacksonSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppTransferTuple {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferTuple.class, args);
	}

	
	@Bean
	public KStream<String, Transfer> tuple(StreamsBuilder builder) {
	  KStream<String, Transfer> stream = 
	      builder.stream(KafkaTopicNames.TRANSFERS_TOPIC, Consumed.with(Serdes.String(), JacksonSerde.of(Transfer.class)));
	  
	  KStream<String, Transfer> rekeyed = 
	      stream.flatMap((key, value) -> 
	          asList(KeyValue.pair(identifier(value.getPoolId(), value.getFrom()), value), 
	                 KeyValue.pair(identifier(value.getPoolId(), value.getTo()), value)));

	  rekeyed.peek((k,v) -> log.info(">>> " + k + " <> " + v));
	  rekeyed.to(KafkaTopicNames.TRANSFER_TUPLES_TOPIC, Produced.with(Serdes.String(), JacksonSerde.of(Transfer.class)));
  	return stream;
	}
}
