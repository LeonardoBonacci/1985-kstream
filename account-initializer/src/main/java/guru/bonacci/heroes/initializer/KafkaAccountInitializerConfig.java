package guru.bonacci.heroes.initializer;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaAccountInitializerConfig {

  public static final String TRANSFERS_TOPIC = "transfers";
  public static final String TRANSFER_TUPLES_TOPIC = "transfer-tuples";
}
