package guru.bonacci.heroes.transfer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import guru.bonacci.heroes.kafka.Transfer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TfService {

  private final TfProducer tfProducer;

  private LoadingCache<String, String> cache = CacheBuilder.newBuilder()
      .maximumSize(Integer.MAX_VALUE)
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .build(new CacheLoader<String, String>() {
          @Override
          public String load(final String response) throws Exception {
              return response;
          }
      });

  
  public boolean transfer(Transfer tf) throws ExecutionException {
    var accKey = tf.getPoolId() + "." + tf.getFrom();
    if (cache.asMap().containsKey(accKey)) {
      throw new TooManyRequestsException("Take a break..");
    }
    cache.put(accKey, "");

    return tfProducer.transfer(tf);
  }
  
  @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
  public static class TooManyRequestsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooManyRequestsException() {
        super();
    }
    
    public TooManyRequestsException(String message) {
        super(message);
    }
  }
}
