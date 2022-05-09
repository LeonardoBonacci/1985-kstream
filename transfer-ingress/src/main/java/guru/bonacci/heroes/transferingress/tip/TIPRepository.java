package guru.bonacci.heroes.transferingress.tip;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TIPRepository {
  
  private static final Long TTL_IN_SEC = 10l;
  private static final String LOCK_PREFIX = "aaaaa-";
  
  @Autowired @Qualifier("trans")
  private StringRedisTemplate writeTemplate;

  @Autowired @Qualifier("no-trans")
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
  
  Boolean lock(String lockId) {
    boolean newKey = readTemplate.opsForValue()
                          .setIfAbsent(LOCK_PREFIX + lockId, lockId, Duration.ofSeconds(TTL_IN_SEC));
    log.info("new lock {}: {}", lockId, newKey);
    return newKey;
  }
}