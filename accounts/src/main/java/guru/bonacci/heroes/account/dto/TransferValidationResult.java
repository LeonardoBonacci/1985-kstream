package guru.bonacci.heroes.account.dto;

import lombok.Value;

@Value
public class TransferValidationResult {

  private boolean poolIsValid;
  private boolean fromIsValid;
  private boolean toIsValid;
  private boolean hasSufficientFunds;
}
