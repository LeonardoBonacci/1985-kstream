package guru.bonacci.heroes.tippurger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafka
@SpringBootApplication
@RequiredArgsConstructor
public class AppTIPPurger {
  
  private final TIPRepository tipRepo;
  

  public static void main(String[] args) {
    SpringApplication.run(AppTIPPurger.class, args);
  }
  
  
  @KafkaListener(topics = KafkaTopicNames.TRANSFER_PROCESSED_TOPIC, 
                 groupId = "us-and-them", 
                 properties = {"isolation.level:read_committed"})
  public void listenToPinkFloyd(ConsumerRecord<String, Transfer> record) {

    final var poolAccountId = record.key();
    final var transfer = record.value();
    log.info("in {}<>{}", poolAccountId, transfer);
    
    // key:value is poolAccountId:transferId
    var transferId = tipRepo.getValue(poolAccountId);
   
    // remove only if transfer id corresponds
    if (transfer.getTransferId().equals(transferId)) {

      boolean deleted = tipRepo.delete(poolAccountId);
      log.info("deleted lock for poolAccountId {} => {}", poolAccountId, deleted);
    } else {
      
      log.warn("-----BIG WARNING------");
      log.warn("Expected {}:{}", poolAccountId, transfer.getTransferId());
      log.warn("But found {}:{}", poolAccountId, transferId);
      log.warn("Consumer rebalance has created a dangerous - but anticipated - situation");
      log.warn("----------------------");
    }
  }
}  
  
