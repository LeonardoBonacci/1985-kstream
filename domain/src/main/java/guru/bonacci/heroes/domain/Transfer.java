package guru.bonacci.heroes.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer implements Cloneable {

  private String transferId; //required
  private String poolId; //required
  private String from; //required
  private String to; //required
  private BigDecimal amount; //required
  private long when; //required
  
  
  public String poolAccountId() {
    return this.poolId + "." + this.from;
  }
  
  public Transfer negativeClone() {
    try {
      return ((Transfer)this.clone()).negate();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new RuntimeException("cloning...");
    }
  }
  
  private Transfer negate() {
    this.setAmount(this.getAmount().negate());
    return this;
  }
}
