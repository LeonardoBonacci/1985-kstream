package guru.bonacci.heroes.tippurger;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafka
@SpringBootApplication
@RequiredArgsConstructor
public class BootstrAppTIPPurger {
  
  private final TIPRepository tipRepo;
  

  public static void main(String[] args) {
    SpringApplication.run(BootstrAppTIPPurger.class, args);
  }
  
  
  @KafkaListener(topics = KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC, 
                 groupId = "us-and-them", 
                 properties = {"isolation.level:read_committed"})
  public void listen(ConsumerRecord<String, Transfer> record) {
    
    final var transfer = record.value();
    log.info("in {}", transfer);
    
    var tipKeys = tipKeys(transfer);
    var tips = tipRepo.getByIds(tipKeys);
    // remove from redis only if transfer id corresponds
    tips.removeIf(tipTransferId -> !transfer.getTransferId().equals(tipTransferId.getTransferId()));
    
    if (tips.size() != 2) {
      log.warn("-----BIG WARNING------");
      log.warn(tips.toString());
      log.warn("consumer rebalance has created a dangerous - but anticipated - situation");
      log.warn("----------------------");
    }
    
    tipRepo.delete(tips);
  }
  

  private List<String> tipKeys(Transfer transfer) {
    return Arrays.asList(
        Account.identifier(transfer.getPoolId(), transfer.getFrom()),
        Account.identifier(transfer.getPoolId(), transfer.getTo()));
  }
}  
  
