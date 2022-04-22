package guru.bonacci.heroes.transfer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.kafka.Transfer;
import lombok.RequiredArgsConstructor;

//@Component
//@RequiredArgsConstructor
public class TxProducer {

//  public static final String TOPIC = "transfers";
//
//  private final KafkaTemplate<String, Transaction> kafkaTemplate;
//
//  
//  public boolean transfer(Transaction tx) {
//    return sendMessage(TOPIC, null, tx);
//  }
// 
//  // exposed for testing
//  boolean sendMessage(String topic, String key, Transaction message) {
//    try {
//      return kafkaTemplate.send(topic, key, message).get().getRecordMetadata().hasOffset();
//    } catch (Throwable t) {
//      t.printStackTrace();
//      return false;
//    }
//  }
}
