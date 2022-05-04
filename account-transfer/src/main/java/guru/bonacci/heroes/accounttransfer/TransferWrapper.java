package guru.bonacci.heroes.accounttransfer;

import guru.bonacci.heroes.domain.Transfer;
import lombok.Value;

@Value
public class TransferWrapper {

  private boolean contains;
  private Transfer transfer;
}
