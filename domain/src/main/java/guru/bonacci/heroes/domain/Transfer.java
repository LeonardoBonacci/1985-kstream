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
public class Transfer {

  private String transferId;
  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
  private long when;
  
  
  public String poolAccountId() {
    return this.poolId + "." + this.from;
  }
}
