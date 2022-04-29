package guru.bonacci.heroes.transfers.validate;

import lombok.Value;

@Value
public class TransferValidationResult {

  private boolean poolIsValid;
  private boolean fromIsValid;
  private boolean toIsValid;
  private boolean hasSufficientFunds;
}
