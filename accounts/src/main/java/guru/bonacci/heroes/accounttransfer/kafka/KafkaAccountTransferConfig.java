package guru.bonacci.heroes.accounttransfer.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

import guru.bonacci.heroes.kafka.KafkaTopicNames;

@Configuration
@EnableKafkaStreams
public class KafkaAccountTransferConfig {

  @Bean
  public NewTopic accountTransfers() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC)
      .partitions(1)
      .build();
  }
  
  @Bean
  public NewTopic transferTuples() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFER_TUPLES_TOPIC)
      .partitions(1)
      .build();
  }
  
  @Bean
  public NewTopic transfersProcessed() {
    return TopicBuilder.name(KafkaTopicNames.TRANSFERS_EVENTUAL_TOPIC)
      .partitions(1)
      .build();
  }
}
