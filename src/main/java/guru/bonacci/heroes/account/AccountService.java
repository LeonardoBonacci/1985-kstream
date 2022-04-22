package guru.bonacci.heroes.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class AccountService {

  // ledger cache
  private Map<String, Account> accounts = new HashMap<>();

  
  public Optional<Account> showMeTheAccount(String accountId) {
    return Optional.ofNullable(accounts.get(accountId));
  }
  
  
  public void processTx(Transaction tx) {
    var from = tx.getFrom();
    if (!accounts.containsKey(from)) {
      accounts.put(from, new Account(from));
    }
    accounts.get(from).getTransactions().add(tx);
    
    var to = tx.getTo();
    if (!accounts.containsKey(to)) {
      accounts.put(to, new Account(to));
    }
    accounts.get(to).getTransactions().add(tx);
  }
  
  public Integer getBalance(String accountId) {
    var account = showMeTheAccount(accountId).get();
    return addUpTxs(account).reduce(0, Integer::sum);
  }
  
  private static Stream<Integer> addUpTxs(Account account) {
    return account.getTransactions().stream()
            .map(tx -> tx.getTo().equals(account.getAccountId()) ? tx.getAmount() : -1 * tx.getAmount());
  }
}
