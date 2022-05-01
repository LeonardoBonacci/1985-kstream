package guru.bonacci.heroes.cdc;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import guru.bonacci.heroes.kafka.KafkaTopicNames;

@Configuration
@EnableKafka
public class KafkaTopicsConfig {

  @Bean
  public NewTopic accountCDC() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNTS_TOPIC)
      .partitions(1)
      .build();
  }
  
  @Bean
  public NewTopic accounts() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC)
      .partitions(1)
      .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
      .build();
  }
}
