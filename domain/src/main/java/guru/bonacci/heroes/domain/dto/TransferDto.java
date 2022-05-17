package guru.bonacci.heroes.domain.dto;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class TransferDto {

  private String poolId;

  private String from;

  private String to;

  private BigDecimal amount;
}