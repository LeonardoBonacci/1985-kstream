package guru.bonacci.heroes.simulator;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class Bookkeeper {

  private final AccountStub mockOrStub;
  
  @Value("${account.url}")
  private String clientHostAndPort;
  
  
  public BigDecimal determineBalance(Account account) {
    return account.getTransfers().stream()
                    .map(tf -> tf.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
  
 
  @Scheduled(fixedRate = 10000)
  public void print() {
    for (String pool : mockOrStub.pools) {
      printPool(mockOrStub.accounts.get(pool), pool);
    }
  }    
  
  private void printPool(Set<String> accountNames, final String poolId) {
    BigDecimal totalBalance = BigDecimal.ZERO;
    
    RestTemplate restTemplate = new RestTemplate();

    for (String accountId : accountNames) {
      var url = clientHostAndPort + "/pools/" + poolId + "/accounts/" + accountId;
      log.info("hitting {}", url);
      ResponseEntity<Account> response = restTemplate.getForEntity(url, Account.class);
  
      var account = response.getBody();
      account.getTransfers().forEach(t -> log.debug("{}", t.getAmount()));
      var computedBalance = determineBalance(account);
  
      if (!computedBalance.equals(account.getBalance())) {
        log.error("account {} balance out of sync {} vs {}", account.getAccountId(), computedBalance, account.getBalance());
        System.exit(1);
      }
      
      log.info("balance for {} is {}", account.getAccountId(), computedBalance);
      totalBalance = totalBalance.add(computedBalance);
    }

    log.warn("TOTAL BALANCE IS {}", totalBalance);
    if (totalBalance.compareTo(BigDecimal.ZERO) != 0) {
        log.error("TOTAL BALANCE IS {}", totalBalance);
    }
  }
}
