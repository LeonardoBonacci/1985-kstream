package guru.bonacci.heroes.transferingress.validation;

import java.math.BigDecimal;

import javax.validation.GroupSequence;

import guru.bonacci.heroes.domain.Transfer;
import lombok.Value;

@LockConstraint(
  groups = CheckLock.class,
  pool = "poolId", 
  from = "from", 
  to = "to"
)
@TransferConstraint(
  groups = CheckTransfer.class,
  pool = "poolId", 
  from = "from", 
  to = "to", 
  amount = "amount"
)
@Value
@GroupSequence({CheckLock.class, CheckTransfer.class, TransferToValidate.class})
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
