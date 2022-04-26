package guru.bonacci.heroes.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

  public static final String TRANSFERS = "transfers";
  public static final String ACCOUNTS = "accounts";


  @Bean
  public NewTopic transfers() {
    return TopicBuilder.name(TRANSFERS)
      .partitions(1)
      .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
      .build();
  }
  
  @Bean
  public NewTopic accounts() {
    return TopicBuilder.name(ACCOUNTS)
      .partitions(1)
      .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
      .build();
  }

}
