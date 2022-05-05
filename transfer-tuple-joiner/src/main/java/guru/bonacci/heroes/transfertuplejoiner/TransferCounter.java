package guru.bonacci.heroes.transfertuplejoiner;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCounter {

  private int counter;
  private Transfer transfer;
  
  public TransferCounter(TransferCounter one, TransferCounter two) {
    this.counter = one.counter + two.counter; // adds one
    this.transfer = two.getTransfer(); // same
  }
  
  public static TransferCounter from(Transfer t) {
    return new TransferCounter(1, t);
  }
}
