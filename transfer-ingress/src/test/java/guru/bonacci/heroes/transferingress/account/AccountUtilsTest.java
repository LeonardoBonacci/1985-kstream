package guru.bonacci.heroes.transferingress.account;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
 
public class AccountUtilsTest {
 
  @Test
  void shouldPerformBasicAccounting() throws Exception {
    var transfers = new ArrayList<>(Lists.list(
      new Transfer("1", "coro", "a", "b", valueOf(100), System.currentTimeMillis()),
      new Transfer("2", "coro", "a", "c", valueOf(50), System.currentTimeMillis()),
      new Transfer("3", "coro", "d", "a", valueOf(20), System.currentTimeMillis()),
      new Transfer("4", "coro", "e", "a", valueOf(20), System.currentTimeMillis())
    ));  
    var account = Account.builder().poolId("coro").accountId("a").transfers(transfers).build();

    assertThat(AccountUtils.getBalance(account)).isEqualTo(valueOf(-110));
    
  }
}  
