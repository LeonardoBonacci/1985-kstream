package guru.bonacci.heroes.accountstore.rpc;

import guru.bonacci.heroes.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueBean {

  private String key;
  private Account value;
}
