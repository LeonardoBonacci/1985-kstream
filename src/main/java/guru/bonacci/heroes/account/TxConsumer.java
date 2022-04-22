package guru.bonacci.heroes.account;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_MESSAGE_KEY;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.kafka.Transfer;
import guru.bonacci.heroes.transfer.TxProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@Component
//@RequiredArgsConstructor
public class TxConsumer {

//  private final AccountService accountService;
//  
//
//  @KafkaListener(topics = {TxProducer.TOPIC})
//  public void listen(@Payload Transaction tx, 
//                     @Header(name = RECEIVED_MESSAGE_KEY, required = false) String poolId) {
//    log.info("Received a message: {} at {}", poolId, tx);
//    accountService.processTx(tx);
//  }
}
