package guru.bonacci.heroes.pool;

import java.util.Collections;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class PoolService {

  private Set<String> pools = Collections.singleton("heroes");

  
  public boolean contains(String poolId) {
    return pools.contains(poolId);
  }
}
