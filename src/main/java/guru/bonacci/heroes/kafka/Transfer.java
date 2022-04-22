package guru.bonacci.heroes.kafka;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class Transfer {

  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
  private long when;
}
