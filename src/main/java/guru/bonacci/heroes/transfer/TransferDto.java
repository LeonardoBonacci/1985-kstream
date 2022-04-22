package guru.bonacci.heroes.transfer;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.Value;

@Value
public class TransferDto {

  @NotBlank(message = "from is mandatory")
  private String from;

  @NotBlank(message = "to is mandatory")
  private String to;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer=3, fraction=2)
  private BigDecimal amount;
}
