package guru.bonacci.heroes.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
  private long when;
}
