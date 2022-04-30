package guru.bonacci.heroes.initializer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountProducer {

  private final KafkaTemplate<String, Account> kafkaTemplate;

  
  public boolean send(Account account) {
    return sendMessage(KafkaTopicNames.ACCOUNTS_TOPIC, account.getAccountId(), account);
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
