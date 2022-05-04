package guru.bonacci.heroes.transferingress.pool;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

@Repository
public class PoolRepository {

  private Map<String, PoolType> pools;
  
  
  @PostConstruct
  void init() {
    pools = new HashMap<>();
    pools.put("coro", PoolType.SARDEX);
  }
  
  public PoolType getType(String poolId) {
    return pools.get(poolId);
  }
}
