package guru.bonacci.heroes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferValidationResponse {

  private Boolean poolIsValid;
  private Boolean fromIsValid;
  private Boolean toIsValid;
  private Account fromAccount;
  
  private String errorMessage; // could become error code?
  
//  public static TransferValidationResult from(Account account) {
//      return new TransferValidationResult(true, true, true, true);
//  }
}
