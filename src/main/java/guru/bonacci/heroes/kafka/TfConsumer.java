package guru.bonacci.heroes.kafka;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_MESSAGE_KEY;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.service.AccService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TfConsumer {

  private final AccService accService;
  

  @KafkaListener(topics = KafkaConfig.TRANSFERS, groupId = "us")
  public void listen(@Payload Transfer tf, 
                     @Header(name = RECEIVED_MESSAGE_KEY, required = false) String poolId) {
    log.info("Received : {} at {}", poolId, tf);
    accService.process(tf);
  }
}
