package guru.bonacci.heroes.transferbigbrother;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferTupleCounter {

  private int counter;
  private TransferTuple transferTuple;
  
  public TransferTupleCounter(TransferTupleCounter one, TransferTupleCounter two) {
    this.counter = one.counter + two.counter; // adds one
    this.transferTuple = two.getTransferTuple(); // take the last/current
  }
  
  public static TransferTupleCounter from(TransferTuple tt) {
    return new TransferTupleCounter(1, tt);
  }
}
