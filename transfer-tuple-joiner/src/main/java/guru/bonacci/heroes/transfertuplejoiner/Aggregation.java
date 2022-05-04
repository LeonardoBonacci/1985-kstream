package guru.bonacci.heroes.transfertuplejoiner;

import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aggregation {

  private boolean paired = false;
  private Transfer transfer;
  
  public Aggregation(Aggregation one, Aggregation two) {
    this.paired = two.isPaired();
    this.transfer = two.getTransfer();
  }
  
  public Aggregation updateFrom(Transfer tr) {
    this.paired = true;
    this.transfer = tr;
    return this;
  }
}
