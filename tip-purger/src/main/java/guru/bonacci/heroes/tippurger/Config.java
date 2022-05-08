package guru.bonacci.heroes.tippurger;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class Config {
  
  
//  @Bean
//  public ConsumerAwareRebalanceListener consumerRebalancer() {
//    return new RedisOffsetConsumerRebalancer();
//  }
//  
//  @Bean //maybe...
//  public ConcurrentKafkaListenerContainerFactoryConfigurer kafkaListenerContainerFactoryConfigurer(
//          ConsumerAwareRebalanceListener rebalanceListener) {
//
//      return new ConcurrentKafkaListenerContainerFactoryConfigurer() {
//
//        @Override
//          public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerContainerFactory,
//                  ConsumerFactory<Object, Object> consumerFactory) {
//              super.configure(listenerContainerFactory, consumerFactory);
//              listenerContainerFactory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener);
//          }
//
//      };
//  }
}
