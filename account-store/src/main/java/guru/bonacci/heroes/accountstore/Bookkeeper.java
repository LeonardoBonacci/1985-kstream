package guru.bonacci.heroes.accountstore;

import java.math.BigDecimal;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class Bookkeeper {

  private final KafkaStreamsService streams;
  
  
  public BigDecimal determineBalance(Account account) {
    return account.getTransfers().stream()
                    .map(tf -> tf.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
  
  @Scheduled(fixedRate = 60000)
  public void print() {
    try {
      BigDecimal totalBalance = BigDecimal.ZERO;
      
      var it = streams.waitForAccountStore().all();
      while(it.hasNext()) {
        var kv = it.next();
        kv.value.getTransfers().forEach(t -> log.debug("{}", t.getAmount()));
        var balance = determineBalance(kv.value);
        log.info("balance for {} is {}", kv.value.getAccountId(), balance);
        totalBalance = totalBalance.add(balance);
      };
      log.warn("TOTAL BALANCE IS {}", totalBalance);
      if (!totalBalance.equals(BigDecimal.ZERO)) {
        log.error("TOTAL BALANCE IS {}", totalBalance);
        System.exit(1);
      }
    } catch(RuntimeException e) {
      e.printStackTrace();
    }
  }    
}
