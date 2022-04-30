package guru.bonacci.heroes.transfers;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import guru.bonacci.heroes.domain.Transfer;

@EnableKafka
@Configuration
@EnableTransactionManagement  
public class KafkaTransfersConfig {

  @Bean
  public ProducerFactory<String, Transfer> producerFactory() {
    DefaultKafkaProducerFactory<String, Transfer> f = new DefaultKafkaProducerFactory<>(senderProps());
    f.setTransactionIdPrefix("tx-");
    return f;
  }

  private Map<String, Object> senderProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "me-again");
    return props;
  }
  
  @Bean
  public KafkaTemplate<String, Transfer> kafkaTemplate() {
    return new KafkaTemplate<String, Transfer>(producerFactory());
  }

  @Bean
  public KafkaTransactionManager<String, Transfer> kafkaTransactionManager() {
    KafkaTransactionManager<String, Transfer> ktm = new KafkaTransactionManager<>(producerFactory());
    ktm.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    return ktm;
  }

  @Bean
  public StringRedisTemplate redisTemplate() {
    StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
    // explicitly enable transaction support
    template.setEnableTransactionSupport(true);              
    return template;
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
    connectionFactory.setDatabase(0);
    connectionFactory.setHostName("localhost");
    connectionFactory.setPort(6379);
    connectionFactory.setPassword("mypass");
    connectionFactory.setTimeout(60000);
    return connectionFactory;
  }
}
