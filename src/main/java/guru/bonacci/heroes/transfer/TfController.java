package guru.bonacci.heroes.transfer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import guru.bonacci.heroes.account.AccService;
import guru.bonacci.heroes.kafka.Transfer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TfController {

  private final AccService accountService;
  private Set<String> pools = Collections.singleton("heroes");
  
  private LoadingCache<String, String> cache = CacheBuilder.newBuilder()
      .maximumSize(Integer.MAX_VALUE)
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .build(new CacheLoader<String, String>() {
          @Override
          public String load(final String response) throws Exception {
              return response;
          }
      });
  
  @PostMapping("/{poolId}")
  public ResponseEntity<Void> transfer(@PathVariable String poolId, @Valid @RequestBody TransferDto dto) throws ExecutionException {
    if (!pools.contains(poolId)) {
      return ResponseEntity.badRequest().build();
    }
    
    if (cache.asMap().containsKey(poolId + "." + dto.getFrom())) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    cache.put(poolId + "." + dto.getFrom(), "");
      
    var tf = toTf(dto, poolId);
    accountService.process(tf);

    return ResponseEntity.noContent().<Void>build();
  }
  
  private Transfer toTf(TransferDto dto, String poolId) {
    return new Transfer(poolId, dto.getFrom(), dto.getTo(), dto.getAmount(), System.currentTimeMillis());
  }
}
