package guru.bonacci.heroes.initializer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import guru.bonacci.heroes.kafka.KafkaTopicNames;

@Configuration
@EnableKafka
public class KafkaAccountInitializerConfig {

  @Bean
  public NewTopic accounts() {
    return TopicBuilder.name(KafkaTopicNames.ACCOUNTS_TOPIC)
      .partitions(1)
      .build();
  }
}
