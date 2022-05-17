package guru.bonacci.heroes.transferingress.transfer;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.GroupSequence;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import guru.bonacci.heroes.transferingress.transfer.validation.ExecFirst;
import guru.bonacci.heroes.transferingress.transfer.validation.ExecSecond;
import guru.bonacci.heroes.transferingress.transfer.validation.FieldsValueDifferent;
import lombok.Value;

@FieldsValueDifferent(
  groups = ExecSecond.class,
  field1 = "from", 
  field2 = "to", 
  message = "'from' and 'to' must be different"
)
@Value
@GroupSequence({TransferDto.class, ExecFirst.class, ExecSecond.class})
public class TransferDto {

  @NotBlank(message = "poolId has to be present")
  private String poolId;

  @NotBlank(message = "from has to be present", groups = ExecFirst.class)
  private String from;

  @NotBlank(message = "to has to be present", groups = ExecFirst.class)
  private String to;

  @Nonnull
  @DecimalMin(value = "0.0", inclusive = false, message = "> 0.0 please")
  @Digits(integer = 4, fraction = 2)
  private BigDecimal amount;
}