package guru.bonacci.heroes.accountcdc;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import guru.bonacci.heroes.kafka.KafkaTopicNames;

@Configuration
@EnableKafka
public class KafkaTopicConfig {

  @Bean
  public NewTopic accounts() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNTS_TOPIC)
      .partitions(2)
      .build();
  }
  
  @Bean
  public NewTopic accountTransfers() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC)
      .partitions(2)
      .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
      .build();
  }
  
  @Bean
  public NewTopic accountStorage() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_STORAGE_SINK_TOPIC)
      .partitions(2)
      .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
      .build();
  }

  @Bean
  public NewTopic transferValidationRequest() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_VALIDATION_REQUEST_TOPIC)
      .partitions(2)
      .config(TopicConfig.RETENTION_MS_CONFIG, "3600000")
      .build();
  }
  
  @Bean
  public NewTopic transferValidationResponse() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_VALIDATION_REPLIES_TOPIC)
      .partitions(2)
      .config(TopicConfig.RETENTION_MS_CONFIG, "3600000")
      .build();
  }
  
  @Bean
  public NewTopic transferTuples() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_TUPLES_TOPIC)
      .partitions(2)
      .build();
  }

}
