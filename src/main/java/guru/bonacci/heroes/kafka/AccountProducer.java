package guru.bonacci.heroes.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//TODO @Profile("stream")
public class AccountProducer {

  private final KafkaTemplate<String, Account> kafkaTemplate;

  
  public boolean send(Account account) {
    return sendMessage("accounts", account.identifier(), account); 
  }
 
  // exposed for testing
  boolean sendMessage(String topic, String key, Account message) {
    try {
      return kafkaTemplate.send(topic, key, message).get().getRecordMetadata().hasOffset();
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }
}
