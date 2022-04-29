package guru.bonacci.heroes.account.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaStreams
public class KafkaAccountsConfig {

  public static final String ACCOUNTS_TOPIC = "accounts";
  public static final String TRANSFER_TUPLES_TOPIC = "transfer-tuples";
  

  @Bean
  public NewTopic accounts() {
    return TopicBuilder.name(ACCOUNTS_TOPIC)
      .partitions(1)
      .build();
  }
  
  @Bean
  public NewTopic transferTuples() {
    return TopicBuilder.name(TRANSFER_TUPLES_TOPIC)
      .partitions(1)
      .build();
  }
}
