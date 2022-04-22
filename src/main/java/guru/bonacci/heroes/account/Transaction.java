package guru.bonacci.heroes.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

  private String from;
  private String to;
  private Integer amount;
  //TODO timestamp
  //TODO poolId
  
}
