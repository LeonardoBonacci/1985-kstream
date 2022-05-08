package guru.bonacci.heroes.tippurger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TIPRepository {
  
  @Autowired
  private StringRedisTemplate redisTemplate;
  
  
  public void deleteByIds(Collection<String> ids) {
    log.info("deleting keys {}", ids);
    redisTemplate.delete(ids);
  }
}