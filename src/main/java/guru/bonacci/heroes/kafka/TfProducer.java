package guru.bonacci.heroes.kafka;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.Transferer;
import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("stream")
public class TfProducer implements Transferer {

  private final KafkaTemplate<String, Transfer> kafkaTemplate;

  
  @Override
  public boolean fer(Transfer tf) {
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
