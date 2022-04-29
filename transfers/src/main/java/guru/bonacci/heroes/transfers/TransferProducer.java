package guru.bonacci.heroes.transfers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferProducer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  public boolean send(Transfer tf) {
    return sendMessage(KafkaTransfersConfig.TRANSFERS_TOPIC, tf.getPoolId(), tf);
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
