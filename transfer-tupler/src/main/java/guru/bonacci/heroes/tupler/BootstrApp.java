package guru.bonacci.heroes.tupler;

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

import guru.bonacci.heroes.tupler.serde.JacksonSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrApp {

	public static void main(String[] args) {
		SpringApplication.run(BootstrApp.class, args);
	}

	
	@Bean
	public KStream<String, Transfer> tuple(StreamsBuilder builder) {
	  KStream<String, Transfer> stream = 
	      builder.stream(KafkaTupleTransfersConfig.TRANSFERS_TOPIC, Consumed.with(Serdes.String(), JacksonSerde.of(Transfer.class)));
	  
	  KStream<String, Transfer> rekeyed = 
	      stream.flatMap((key, value) -> 
	          asList(KeyValue.pair(identifier(value.getPoolId(), value.getFrom()), value), 
	                 KeyValue.pair(identifier(value.getPoolId(), value.getTo()), value)));

	  rekeyed.peek((k,v) -> log.info(">>> " + k + " <> " + v));
	  rekeyed.to(KafkaTupleTransfersConfig.TRANSFER_TUPLES_TOPIC, Produced.with(Serdes.String(), JacksonSerde.of(Transfer.class)));
  	return stream;
	}
	
  private static String identifier(String poolId, String accountId) {
    return poolId + "." + accountId;
  }
}
