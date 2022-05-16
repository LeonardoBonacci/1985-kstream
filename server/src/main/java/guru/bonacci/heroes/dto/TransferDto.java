package guru.bonacci.heroes.dto;


import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.Value;

@Value
public class TransferDto {

  @NotBlank(message = "poolId has to be present")
  private String poolId;

  @NotBlank(message = "from has to be present")
  private String from;

  @NotBlank(message = "to has to be present")
  private String to;

  @Nonnull
  @DecimalMin(value = "0.0", inclusive = false, message = "> 0.0 please")
  @Digits(integer = 4, fraction = 2)
  private BigDecimal amount;
}