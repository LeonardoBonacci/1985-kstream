package guru.bonacci.heroes.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  private String accountId;
  private String poolId;

  @Builder.Default
  private final List<Transfer> transactions = new ArrayList<>(); // where from == accountId or to == accountId
  
  
  public String identifier() {
    return poolId + "." + accountId;
  }
}
