package guru.bonacci.heroes.tippurger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import guru.bonacci.heroes.domain.TransferInProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TIPRepository {
  
  private final StringRedisTemplate redisTemplate;
  
  
  public List<TransferInProgress> getByIds(Collection<String> ids) {
    log.info("about to delete keys {}", ids);
    return ids.stream()
        .map(poolAccountId -> {
          var transferId = redisTemplate.opsForValue().get(poolAccountId);
          return new TransferInProgress(poolAccountId, transferId);
        })
        .collect(Collectors.toList());
  }
  
  public Long delete(Collection<TransferInProgress> tips) {
    return deleteByIds(
        tips.stream()
            .map(tip -> tip.getPoolAccountId())
            .collect(Collectors.toList()));
  }

  public Long deleteByIds(Collection<String> ids) {
    log.info("actually deleting keys {}", ids);
    return redisTemplate.delete(ids);
  }
}