package guru.bonacci.heroes.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;

@Service
public class AccService {

  // ledger cache - key is poolId.accountId
  private Map<String, Account> accounts = new HashMap<>();

  public void addAccountToPool(Account acc) {
    accounts.put(acc.identifier(), acc);
  }
  
  
  public Optional<Account> showMeTheAccount(String accountId, String poolId) {
    return Optional.ofNullable(accounts.get(poolId + "." + accountId));
  }
  
  public boolean process(Transfer tf) {
    var from = tf.getFrom();
    var fromKey = tf.getPoolId() + "." + from;
    if (!accounts.containsKey(fromKey)) {
      accounts.put(fromKey, Account.builder().accountId(from).poolId(tf.getPoolId()).build());
    }
    accounts.get(fromKey).getTransactions().add(tf);
    
    var to = tf.getTo();
    var toKey = tf.getPoolId() + "." + to;
    if (!accounts.containsKey(toKey)) {
      accounts.put(toKey, Account.builder().accountId(to).poolId(tf.getPoolId()).build());
    }
    accounts.get(toKey).getTransactions().add(tf);
    return true;
  }
  
  public Optional<BigDecimal> showMeTheBalance(String accountId, String poolId) {
    var accOpt = showMeTheAccount(accountId, poolId);
    return accOpt.map(acc -> addUpTxs(acc).reduce(BigDecimal.ZERO, BigDecimal::add));
  }
  
  private static Stream<BigDecimal> addUpTxs(Account acc) {
    return acc.getTransactions().stream()
            .map(tx -> tx.getTo().equals(acc.getAccountId()) ? tx.getAmount() : tx.getAmount().multiply(BigDecimal.valueOf(-1)));
  }
  
  // exposed for testing
  void cleanSheet() {
    accounts = new HashMap<>();
  }
}
