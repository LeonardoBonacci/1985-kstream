package guru.bonacci.heroes.domain;

import lombok.Value;

@Value
public class TransferValidationResult {

  private boolean poolIsValid;
  private boolean fromIsValid;
  private boolean toIsValid;
  private boolean hasSufficientFunds;
  

  public static TransferValidationResult from(Account account) {
      return new TransferValidationResult(true, true, true, true);
  }
}
