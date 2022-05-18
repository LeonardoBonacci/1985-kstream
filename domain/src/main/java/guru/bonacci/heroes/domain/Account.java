package guru.bonacci.heroes.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = { "transfers" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  private String poolId; //required
  private String accountId; //required

  @Builder.Default
  private final List<Transfer> transfers = new ArrayList<>();
  private BigDecimal balance; //required

  
  // utilities follow below
  public String identifier() {
    return this.poolId + "." + this.accountId;
  }

  public static String identifier(String poolId, String accountId) {
    return poolId + "." + accountId;
  }

  public Account addTransfer(Transfer transfer) {
    transfers.add(transfer);
    balance = balance.add(transfer.getAmount());
    return this;
  }

  public boolean hasTransfer() {
    return !transfers.isEmpty();
  }

  public Transfer latestTransfer() {
    return hasTransfer() ? Iterables.getLast(transfers) : null;
  }
}
