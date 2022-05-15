package guru.bonacci.heroes.transferingress.account;

import java.math.BigDecimal;
import java.util.stream.Stream;

import guru.bonacci.heroes.domain.Account;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountUtils {

  public BigDecimal getBalance(Account account) {
    return addUpTransfers(account).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
  
  private Stream<BigDecimal> addUpTransfers(Account account) {
    return account.getTransfers().stream()
            .map(tf -> tf.getTo().equals(account.getAccountId()) 
                        ? tf.getAmount() 
                        : tf.getAmount().multiply(BigDecimal.valueOf(-1)));
  }
}
