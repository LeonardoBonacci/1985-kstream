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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AccountStub {

  public Map<String, Set<String>> accounts = Maps.newHashMap();


  @KafkaListener(topics = KafkaTopicNames.ACCOUNT_TOPIC, groupId = "simulator")
  public void listen2(@Payload AccountCDC account) {
    log.info("receiving account {}", account);
    
    var poolId = account.getPoolId();

    if (!accounts.containsKey(poolId)) {
      log.warn("pool missing {}", poolId);
      accounts.put(poolId, Sets.newHashSet());
    } 
    
    accounts.get(poolId).add(account.getAccountId());
  }
  
  
  String getRandomPool() {
    var random = new Random();
    List<String> asList
        = accounts.keySet().stream().collect(Collectors.toList());
    return asList.get(random.nextInt(asList.size()));
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
  
  
  @Scheduled(fixedRateString =  "10000")
  public void accounts() {
    for (String pool : accounts.keySet()) {
      log.info("!!!!!!!!!!!!!!!!!!");
      log.info("pool {}", pool);
      accounts.get(pool).forEach(account -> log.info("account {}", account));
      log.info("------------------");
    }
  }
}
