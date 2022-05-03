package guru.bonacci.heroes.transfers.pool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PoolType {

  SARDEX("sardex"); 
  
  @Getter private String name;
}
