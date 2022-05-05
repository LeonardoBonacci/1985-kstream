package guru.bonacci.heroes.transferbigbrother;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCounter {

  private int counter = 0;
  private Transfer transfer;
  
//  public TransferCounter(TransferCounter one, TransferCounter two) {
//    this.paired = two.isPaired();
//    this.transfer = two.getTransfer(); // same
//  }
//  
//  public TransferCounter updateFrom(Transfer tr) {
//    this.paired = this.transfer != null;
//    this.transfer = tr;
//    return this;
//  }
}
