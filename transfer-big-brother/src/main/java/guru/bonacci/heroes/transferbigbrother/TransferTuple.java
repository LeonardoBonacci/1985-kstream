package guru.bonacci.heroes.transferbigbrother;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferTuple {

  private Transfer tLeft;
  private Transfer tRight;
}
