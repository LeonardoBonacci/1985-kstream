package guru.bonacci.heroes.accounttransfers;

import guru.bonacci.heroes.domain.Account;
import lombok.Value;

@Value
public class AccountWrapper {

  private boolean insert;
  private Account account;
}
