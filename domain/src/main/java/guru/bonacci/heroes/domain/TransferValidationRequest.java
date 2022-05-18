package guru.bonacci.heroes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferValidationRequest {

  private String poolId; //required
  private String from; //required
  private String to; //required
}
