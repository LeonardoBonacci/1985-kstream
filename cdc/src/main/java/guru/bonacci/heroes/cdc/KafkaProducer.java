package guru.bonacci.heroes.cdc;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.domain.PoolCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

  private final KafkaTemplate<String, AccountCDC> accountTemplate;
  private final KafkaTemplate<String, PoolCDC> poolTemplate;

  
  public boolean send(AccountCDC account) {
    return sendMessage(KafkaTopicNames.ACCOUNT_TOPIC, account.identifier(), account);
  }

  private boolean sendMessage(String topic, String key, AccountCDC message) {
    try {
      return accountTemplate.send(topic, key, message).get().getProducerRecord().timestamp() != null;
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }
  
  public boolean send(PoolCDC pool) {
    return sendMessage(KafkaTopicNames.POOL_TOPIC, pool.getPoolId(), pool);
  }

  private boolean sendMessage(String topic, String key, PoolCDC message) {
    try {
      return poolTemplate.send(topic, key, message).get().getProducerRecord().timestamp() != null;
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

}
