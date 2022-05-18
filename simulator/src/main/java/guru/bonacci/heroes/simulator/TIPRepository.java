package guru.bonacci.heroes.simulator;

import java.util.stream.Stream;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TIPRepository {
  
  private final StringRedisTemplate redisTemplate;
  
  
  public String getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public Stream<Pair<String, String>> getAll() {
    var allKeys = redisTemplate.keys("*");
    return allKeys.stream()
        .map(key -> Pair.of(key, getValue(key)));
  }
}