package guru.bonacci.heroes.account.domain;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class TransferValidationRequest {

  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
}
