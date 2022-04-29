package guru.bonacci.heroes.initializer.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  private String accountId;
  private String poolId;

  @Builder.Default
  private final List<Transfer> transfers = new ArrayList<>();
  

  public String identifier() {
    return this.poolId + "." + this.accountId;
  }

  public static String identifier(String poolId, String accountId) {
    return poolId + "." + accountId;
  }

  public Account addTransfer(Transfer transfer) {
    transfers.add(transfer);
    return this;
  }
}
