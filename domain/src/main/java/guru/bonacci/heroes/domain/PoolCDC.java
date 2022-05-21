package guru.bonacci.heroes.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolCDC {

  private String poolId;
  private PoolType type; 
}
