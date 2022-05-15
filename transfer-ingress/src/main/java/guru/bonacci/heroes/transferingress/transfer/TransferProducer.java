package guru.bonacci.heroes.transferingress.transfer;

import static guru.bonacci.heroes.domain.Account.*;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_TOPIC;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferProducer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  public Transfer send(Transfer transfer) {
    long timestamp = sendMessage(TRANSFER_TOPIC, identifier(transfer.getPoolId(), transfer.getFrom()), transfer);
    transfer.setWhen(timestamp);
    return transfer;
  }
 
  // exposed for testing
  long sendMessage(String topic, String key, Transfer message) {
    try {
      return kafkaTemplate.send(topic, key, message).get().getRecordMetadata().timestamp();
    } catch (Throwable t) {
      t.printStackTrace();
      return -1;
    }
  }
}
