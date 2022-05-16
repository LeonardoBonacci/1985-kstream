package guru.bonacci.heroes.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountService {

  // ledger cache - key is poolId.accountId
  private Map<String, Account> accounts = new HashMap<>();

  
  public Optional<Account> showAccount(String accountId, String poolId) {
    return Optional.ofNullable(accounts.get(poolId + "." + accountId));
  }
  
  public boolean process(Transfer tf) {
    log.info("in {}", tf);

    var from = tf.getFrom();
    var fromKey = tf.getPoolId() + "." + from;
    if (!accounts.containsKey(fromKey)) {
      accounts.put(fromKey, Account.builder().poolId(tf.getPoolId()).accountId(from).build());
    }
    accounts.get(fromKey).getTransfers().add(tf);
    
    var to = tf.getTo();
    var toKey = tf.getPoolId() + "." + to;
    if (!accounts.containsKey(toKey)) {
      accounts.put(toKey, Account.builder().poolId(tf.getPoolId()).accountId(to).build());
    }
    accounts.get(toKey).getTransfers().add(tf.negativeClone());
    return true;
  }
  
  public Optional<BigDecimal> getBalance(String accountId, String poolId) {
    var accOpt = showAccount(accountId, poolId);
    return accOpt.map(AccountService::determineBalance);
  }
  
  private static BigDecimal determineBalance(Account account) {
    return account.getTransfers().stream()
                    .map(tf -> tf.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
