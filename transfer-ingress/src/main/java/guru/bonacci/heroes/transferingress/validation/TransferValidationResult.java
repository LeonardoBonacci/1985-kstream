package guru.bonacci.heroes.transferingress.validation;

import lombok.Value;

@Value
public class TransferValidationResult {
  
  private boolean isValid;
  private String errorMessage;
}
