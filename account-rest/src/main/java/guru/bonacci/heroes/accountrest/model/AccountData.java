package guru.bonacci.heroes.accountrest.model;

import java.math.BigDecimal;
import java.util.List;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import lombok.Getter;

@Getter
public class AccountData {

  private String poolId;
  private String accountId;
  private List<Transfer> transfers;
  private BigDecimal balance;

  private AccountData(String poolId, String accountId, List<Transfer> transfers) {
      this.poolId = poolId;
      this.accountId = accountId;
      this.transfers = transfers;
      // TODO compute balance
      this.balance = BigDecimal.TEN;
  }

  public static AccountData from(Account account) {
      return new AccountData(
              account.getPoolId(),
              account.getAccountId(),
              account.getTransfers()
      );
  }
}
