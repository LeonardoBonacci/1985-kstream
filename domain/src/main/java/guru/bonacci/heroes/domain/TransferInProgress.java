package guru.bonacci.heroes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferInProgress {

  private String poolAccountId;
  private String transferId;
}
