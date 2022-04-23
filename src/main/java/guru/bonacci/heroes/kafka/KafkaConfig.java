package guru.bonacci.heroes.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
@Profile("stream")
public class KafkaConfig {

  public static final String TOPIC = "transfers";


  @Bean
  public NewTopic topicExample() {
    return TopicBuilder.name(TOPIC)
      .partitions(1)
      .build();
  }
}
