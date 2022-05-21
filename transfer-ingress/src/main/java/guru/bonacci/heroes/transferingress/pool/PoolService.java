package guru.bonacci.heroes.transferingress.pool;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Repository;

import guru.bonacci.heroes.domain.PoolCDC;
import guru.bonacci.heroes.domain.PoolType;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PoolService {

  private Map<String, PoolType> pools;
  

  @KafkaListener(topics = KafkaTopicNames.POOL_TOPIC, groupId = "transfer-ingress",
                 properties = {"auto-offset-reset=earliest"})
  public void listen(@Payload PoolCDC pool) {
    log.info("receiving pool {}", pool);
    pools.put(pool.getPoolId(), pool.getType());
  }

  public PoolType getType(String poolId) {
    return pools.get(poolId);
  }
}
