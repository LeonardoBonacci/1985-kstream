package guru.bonacci.heroes.accountinitializer;

import guru.bonacci.heroes.domain.Account;
import lombok.Value;

@Value
public class AccountWrapper {

  private boolean insert;
  private Account account;
}
