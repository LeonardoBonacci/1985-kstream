package guru.bonacci.heroes.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.service.PoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
//TODO @Profile("stream")
public class AccountConsumer {

  private final PoolService poolService;
  

  @KafkaListener(topics = KafkaConfig.ACCOUNTS, 
                 groupId = "#{T(java.util.UUID).randomUUID().toString()}",
                 properties = {"auto.offset.reset:earliest"})
  public void listen(@Payload Account acc) {
    log.info("Received : {} at {}", acc);
    poolService.addAccountToPool(acc);
  }
}
