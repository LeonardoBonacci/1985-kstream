package guru.bonacci.heroes.integrationtest;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
}