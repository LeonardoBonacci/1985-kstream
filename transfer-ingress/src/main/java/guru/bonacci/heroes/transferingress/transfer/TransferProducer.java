package guru.bonacci.heroes.transferingress.transfer;

import static guru.bonacci.heroes.domain.Account.*;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFERS_TOPIC;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferProducer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  public boolean send(Transfer transfer) {
    return sendMessage(TRANSFERS_TOPIC, identifier(transfer.getPoolId(), transfer.getFrom()), transfer);
  }
 
  // exposed for testing
  boolean sendMessage(String topic, String key, Transfer message) {
    try {
      return kafkaTemplate.send(topic, key, message).get().getRecordMetadata().hasOffset();
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }
}
