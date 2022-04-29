package guru.bonacci.heroes.transfers.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash
public class TransferInProgress {

  @Id
  private String account; //poolId.accountId or poolId.fromId
  private String transferId;
}
