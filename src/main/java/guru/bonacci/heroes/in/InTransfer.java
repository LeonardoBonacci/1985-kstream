package guru.bonacci.heroes.in;

import lombok.Data;

@Data
public class InTransfer {

  private String from;
  private String to;
  private Integer amount;
  //TODO timestamp
  //TODO poolId
  
}
