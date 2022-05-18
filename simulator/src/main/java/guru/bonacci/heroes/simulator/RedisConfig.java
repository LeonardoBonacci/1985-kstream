package guru.bonacci.heroes.simulator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

  @Value("${redis.host}") String redisHost;

  @Bean
  public StringRedisTemplate redisReadTemplate() {
    return new StringRedisTemplate(redisConnectionFactory());
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
}
