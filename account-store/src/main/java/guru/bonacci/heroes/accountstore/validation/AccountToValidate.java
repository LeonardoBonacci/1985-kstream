package guru.bonacci.heroes.accountstore.validation;

import lombok.Value;

@AccountConstraint(
  pool = "poolId", 
  account = "accountId"
)
@Value
public class AccountToValidate {

    private String poolId;
    private String accountId;
}
