package guru.bonacci.heroes.transferingress;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_VALIDATION_RESPONSE_TOPIC;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;

@EnableKafka
@Configuration
//@EnableTransactionManagement  
public class Config {

  @Value("${spring.kafka.bootstrap-servers}") String bootstrapServer;
  @Value("${redis.host}") String redisHost;
  
  
  @Bean("transfer")
  public ProducerFactory<String, Transfer> producerFactory() {
    DefaultKafkaProducerFactory<String, Transfer> f = new DefaultKafkaProducerFactory<>(senderProps());
//    f.setTransactionIdPrefix("tx-");
    return f;
  }

  private Map<String, Object> senderProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "me-again");
    return props;
  }
  
  @Bean
  public KafkaTemplate<String, Transfer> kafkaTemplate(
      @Qualifier("transfer") ProducerFactory<String, Transfer> pf) {
    return new KafkaTemplate<String, Transfer>(pf);
  }
  
  

//  @Bean
//  public KafkaTransactionManager<String, Transfer> kafkaTransactionManager() {
//    KafkaTransactionManager<String, Transfer> ktm = new KafkaTransactionManager<>(producerFactory());
//    ktm.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
//    return ktm;
//  }
//
  @Bean("locker")
  public StringRedisTemplate redisLockTemplate() {
    StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
    return template;
  }

  @Bean("writer")
  public StringRedisTemplate redisWriteTemplate() {
    StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
    // explicitly enable transaction support
//    template.setEnableTransactionSupport(true);        
    return template;
  }

  @Bean("reader")
  public StringRedisTemplate redisReadTemplate() {
    StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
    // explicitly enable transaction support
//    template.setEnableTransactionSupport(true);              
    return template;
  }

  @SuppressWarnings("deprecation")
  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
    connectionFactory.setDatabase(0);
    connectionFactory.setHostName(redisHost);
    connectionFactory.setPort(6379);
    connectionFactory.setPassword("mypass");
    connectionFactory.setTimeout(60000);
    return connectionFactory;
  }
  
  @Bean("validation")
  public ProducerFactory<String, TransferValidationRequest> validationProducerFactory() {
    return new DefaultKafkaProducerFactory<>(validationSenderProps());
  }

  private Map<String, Object> validationSenderProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return props;
  }

  @Bean
  public ReplyingKafkaTemplate<String, TransferValidationRequest, TransferValidationResponse> replyingTemplate(
      @Qualifier("validation") ProducerFactory<String, TransferValidationRequest> pf,
      ConcurrentMessageListenerContainer<String, TransferValidationResponse> repliesContainer) {

    return new ReplyingKafkaTemplate<>(pf, repliesContainer);
  }

  @Bean
  public ConcurrentMessageListenerContainer<String, TransferValidationResponse> repliesContainer(
      ConcurrentKafkaListenerContainerFactory<String, TransferValidationResponse> containerFactory) {

    ConcurrentMessageListenerContainer<String, TransferValidationResponse> repliesContainer = 
        containerFactory.createContainer(TRANSFER_VALIDATION_RESPONSE_TOPIC);
    repliesContainer.getContainerProperties().setGroupId(UUID.randomUUID().toString()); // unique
    repliesContainer.setAutoStartup(false);

    Properties props = new Properties();
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
    props.setProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, Account.class.getName());
    repliesContainer.getContainerProperties().setKafkaConsumerProperties(props);
    return repliesContainer;
  }
}
