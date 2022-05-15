package guru.bonacci.heroes.accountinitializer;

import guru.bonacci.heroes.domain.Account;
import lombok.Value;

@Value
public class AccountUpsert {

  private boolean insert;
  private Account account;
}
