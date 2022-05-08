package guru.bonacci.heroes.transferingress.tip;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

// https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#tx
@Repository
public class TIPRepository {
  
  @Autowired @Qualifier("writer")
  private StringRedisTemplate writeTemplate;

  @Autowired @Qualifier("reader")
  private StringRedisTemplate readTemplate;


  boolean existsById(String id) {
    return readTemplate.hasKey(id);
  }
  
  TransferInProgress save(TransferInProgress tip) {
    writeTemplate.opsForValue().set(tip.getPoolAccountId(), tip.getTransferId());
    return tip;
  }
  
  Map<String, TransferInProgress> saveAll(Map<String, TransferInProgress> tips) {
    var tipsAsString = Maps.transformValues(tips, tip -> tip.getTransferId());
    writeTemplate.opsForValue().multiSet(tipsAsString);
    return tips;
  }
}