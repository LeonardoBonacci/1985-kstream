package guru.bonacci.heroes.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TfProducer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  public boolean send(Transfer tf) {
    return sendMessage(KafkaConfig.TRANSFERS, tf.getPoolId(), tf);
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
