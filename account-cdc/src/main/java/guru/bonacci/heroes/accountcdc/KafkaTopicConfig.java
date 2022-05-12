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
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_TOPIC)
      .partitions(2)
      .build(); // TODO infinite retention
  }
  
  @Bean
  public NewTopic accountTransfers() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC)
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
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_VALIDATION_RESPONSE_TOPIC)
      .partitions(2)
      .config(TopicConfig.RETENTION_MS_CONFIG, "3600000")
      .build();
  }

  @Bean
  public NewTopic transfers() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_TOPIC)
      .partitions(2)
      .build(); // TODO infinite retention
  }

  @Bean
  public NewTopic transferPairs() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_PAIR_TOPIC)
      .partitions(2)
      .build();
  }

  @Bean
  public NewTopic transferEventual() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC)
      .partitions(2)
      .build();
  }

  @Bean
  public NewTopic transferConsistent() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC)
      .partitions(2)
      .build();
  }
  
  @Bean
  public NewTopic transferHouston() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_HOUSTON_TOPIC)
      .partitions(2)
      .build();
  }
}
