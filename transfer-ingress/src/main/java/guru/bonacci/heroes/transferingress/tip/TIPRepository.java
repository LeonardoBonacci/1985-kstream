package guru.bonacci.heroes.transferingress.tip;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

import guru.bonacci.heroes.domain.TransferInProgress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TIPRepository {
  
  public static final String LOCK_KEY_PREFIX = "###-";

  @Value("${spring.mvc.async.request-timeout}")
  private Long ttlInMs;

  
  @Autowired @Qualifier("tx")
  private StringRedisTemplate writeTemplate;

  @Autowired @Qualifier("no-tx")
  private StringRedisTemplate readTemplate;


  public boolean existsById(String id) {
    return readTemplate.hasKey(id);
  }
  
  public TransferInProgress save(TransferInProgress tip) {
    writeTemplate.opsForValue().set(tip.getPoolAccountId(), tip.getTransferId());
    return tip;
  }
  
  public Map<String, TransferInProgress> saveAll(Map<String, TransferInProgress> tips) {
    var tipsAsString = Maps.transformValues(tips, tip -> tip.getTransferId());
    writeTemplate.opsForValue().multiSet(tipsAsString);
    return tips;
  }
  
  public Boolean lock(String lockId) {
    boolean newKey = readTemplate.opsForValue()
                          .setIfAbsent(LOCK_KEY_PREFIX + lockId, lockId, Duration.ofMillis(ttlInMs));
    log.info("new lock {}: {}", lockId, newKey);
    return newKey;
  }
}