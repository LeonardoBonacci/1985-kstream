package guru.bonacci.heroes.transferingress.validate;

import lombok.Value;

@Value
public class TransferValidationResult {
  
  private boolean isValid;
  private String errorMessage;
}
