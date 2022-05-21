package guru.bonacci.heroes.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.domain.PoolCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountStub {

  public List<String> pools = Lists.newArrayList();
  public Map<String, Set<String>> accounts = Maps.newHashMap();


  @KafkaListener(topics = KafkaTopicNames.POOL_TOPIC, groupId = "simulator")
  public void listen(@Payload PoolCDC pool) {
    log.info("receiving pool {}", pool);
    pools.add(pool.getPoolId());

    if (!accounts.containsKey(pool.getPoolId())) {
      accounts.put(pool.getPoolId(), Sets.newHashSet());
    }  
  }

  @KafkaListener(topics = KafkaTopicNames.ACCOUNT_TOPIC, groupId = "simulator")
  public void listen2(@Payload AccountCDC account) {
    log.info("receiving account {}", account);
    
    var poolId = account.getPoolId();

    if (!pools.contains(poolId)) {
      log.warn("pool missing {}", poolId);
      log.warn(pools.toString());
      return;
    } 
    
    accounts.get(poolId).add(account.getAccountId());
  }
  
  
  String getRandomPool() {
    var random = new Random();
    return pools.get(random.nextInt(pools.size()));
  }

  String getRandomAccount(String poolId) {
    var random = new Random();
    var accountsAsList = accounts.get(poolId).stream().collect(Collectors.toList());
    return accountsAsList.get(random.nextInt(accountsAsList.size()));
  }
  
  BigDecimal getRandomAmount(int range) {
    BigDecimal max = new BigDecimal(range);
    BigDecimal randFromDouble = new BigDecimal(Math.random());
    BigDecimal actualRandomDec = randFromDouble.multiply(max);
    actualRandomDec = actualRandomDec.setScale(2, RoundingMode.DOWN);
    return actualRandomDec;
  }
}
