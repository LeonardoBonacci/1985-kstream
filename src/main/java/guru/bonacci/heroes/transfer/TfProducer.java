package guru.bonacci.heroes.transfer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.kafka.KafkaConfig;
import guru.bonacci.heroes.kafka.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TfProducer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  public boolean transfer(Transfer tf) {
    return sendMessage(KafkaConfig.TOPIC, tf.getPoolId(), tf);
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
