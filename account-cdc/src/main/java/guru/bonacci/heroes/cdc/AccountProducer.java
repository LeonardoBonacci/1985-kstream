package guru.bonacci.heroes.cdc;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountProducer {

  private final KafkaTemplate<String, AccountCDC> kafkaTemplate;

  
  public boolean send(AccountCDC account) {
    return sendMessage(KafkaTopicNames.ACCOUNTS_TOPIC, account.identifier(), account);
  }
 
  private boolean sendMessage(String topic, String key, AccountCDC message) {
    try {
      return kafkaTemplate.send(topic, key, message).get().getProducerRecord().timestamp() != null;
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }
}
