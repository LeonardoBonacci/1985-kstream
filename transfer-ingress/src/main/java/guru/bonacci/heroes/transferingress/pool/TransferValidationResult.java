package guru.bonacci.heroes.transferingress.pool;

import lombok.Value;

@Value
public class TransferValidationResult {
  
  private boolean isValid;
  private String errorMessage;
}
