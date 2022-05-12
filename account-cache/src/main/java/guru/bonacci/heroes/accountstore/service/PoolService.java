package guru.bonacci.heroes.accountstore.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoolService {
  
  // key is poolId
  // value is list of accountId -> representing members in a pool
  private Map<String, Set<String>> poolCache = new HashMap<>(); 

  
  @KafkaListener(topics = KafkaTopicNames.ACCOUNT_TOPIC, 
                 groupId = "#{T(java.util.UUID).randomUUID().toString()}") // each instance loads all accounts
  public void listen(@Payload AccountCDC account) throws IOException {
    log.info("Loading into pool: {}", account);
    addAccountToPool(account);
  }
  
  public void addAccountToPool(AccountCDC account) {
    poolCache.putIfAbsent(account.getPoolId(), new HashSet<String>());
    poolCache.get(account.getPoolId()).add(account.getAccountId());
  }
  
  public boolean exists(String poolId) {
    return poolCache.containsKey(poolId);
  }

  public boolean containsAccount(String poolId, String accountId) {
    return exists(poolId) && poolCache.get(poolId).contains(accountId);
    
  }

  public boolean notContainsAccount(String poolId, String accountId) {
    return !containsAccount(poolId, accountId);
  }
}
