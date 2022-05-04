package guru.bonacci.heroes.transferingress.pool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PoolType {

  SARDEX("sardex"); 
  
  @Getter private String name;
}
