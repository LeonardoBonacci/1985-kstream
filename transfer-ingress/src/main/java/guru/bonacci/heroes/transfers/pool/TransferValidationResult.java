package guru.bonacci.heroes.transfers.pool;

import lombok.Value;

@Value
public class TransferValidationResult {
  
  private boolean isValid;
  private String errorMessage;
}
