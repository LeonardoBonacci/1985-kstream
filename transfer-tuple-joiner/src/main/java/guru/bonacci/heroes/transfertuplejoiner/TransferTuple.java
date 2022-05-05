package guru.bonacci.heroes.transfertuplejoiner;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferTuple {

  private boolean paired = false;
  private Transfer transfer;
  
  public TransferTuple(TransferTuple one, TransferTuple two) {
    this.paired = two.isPaired();
    this.transfer = two.getTransfer(); // same
  }
  
  public TransferTuple updateFrom(Transfer tr) {
    this.paired = this.transfer != null;
    this.transfer = tr;
    return this;
  }
}
