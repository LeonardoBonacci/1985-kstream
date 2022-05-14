package guru.bonacci.heroes.transferingress.validation;

import java.math.BigDecimal;

import guru.bonacci.heroes.domain.Transfer;
import lombok.Value;

@TransferConstraint(
  pool = "poolId", 
  from = "from", 
  to = "to", 
  amount = "amount"
)
@Value
public class TransferToValidate {

  private String transferId;
  private String poolId;
  private String from;
  private String to;
  private BigDecimal amount;
  
  
  public static TransferToValidate from(Transfer t) {
    return new TransferToValidate(t.getTransferId(), t.getPoolId(), t.getFrom(), t.getTo(), t.getAmount());
  }
}
