package guru.bonacci.heroes.domain;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;

import lombok.Value;

@Value
public class TransferDto {

  @NotBlank(message = "poolId is mandatory")
  private String poolId;

  @NotBlank(message = "from is mandatory")
  private String from;

  @NotBlank(message = "to is mandatory")
  private String to;

  @Nonnull
  private BigDecimal amount;
}