package guru.bonacci.heroes.transfers;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

  private final TransferProducer tfProducer;

  private LoadingCache<String, String> cache = CacheBuilder.newBuilder()
      .maximumSize(Integer.MAX_VALUE)
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .build(new CacheLoader<String, String>() {
          @Override
          public String load(final String response) throws Exception {
              return response;
          }
      });
  

  public boolean transfer(Transfer tf) {
    var accKey = tf.getPoolId() + "." + tf.getFrom();
    if (cache.asMap().containsKey(accKey)) {
      throw new TooManyRequestsException("Take a break..");
    }
    cache.put(accKey, "");

    return tfProducer.send(tf);
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
