package guru.bonacci.heroes.transfers;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaTransfersConfig {

  public static final String TRANSFERS_TOPIC = "transfers";
}
