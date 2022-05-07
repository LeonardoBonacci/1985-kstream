package guru.bonacci.heroes.transfertuplejoiner;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferTuple {

  private Transfer t1;
  private Transfer t2;
}
