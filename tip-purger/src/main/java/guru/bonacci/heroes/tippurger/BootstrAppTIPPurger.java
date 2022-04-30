package guru.bonacci.heroes.tippurger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BootstrAppTIPPurger {

  private final TIPCache tipCache;
  private final KafkaOffsetCache offsets;
  private final RedisOffsetConsumerRebalancer rebalancer;
  
  private final PlatformTransactionManager transactionManager;
  
	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTIPPurger.class, args);
	}
	
	
  @KafkaListener(topics = KafkaTopicNames.TRANSFERS_CONSISTENT_TOPIC, groupId = "us")
  public void listen(ConsumerRecord<String, Transfer> record) {
    var transfer = record.value();
    log.info("Received : {}", transfer);

    final var topicPartition = new TopicPartition(record.topic(), record.partition());
    final var lastOffsetMetadata = rebalancer.getCurrentOffsets(topicPartition);

    var txDefinition = new DefaultTransactionDefinition();
    txDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
    var txStatus = transactionManager.getTransaction(txDefinition);

    try {
      tipCache.deleteById(Transfer.identifier(transfer.getPoolId(), transfer.getFrom()));
      offsets.save(new KafkaOffset(KafkaOffset.key(record.topic(), record.partition()), record.offset()));
      rebalancer.addOffsetToTrack(record.topic(), record.partition(), record.offset());

      transactionManager.commit(txStatus);
    } catch (Exception ex) {
        transactionManager.rollback(txStatus);
        rebalancer.addOffsetToTrack(topicPartition, lastOffsetMetadata); //TODO add this to tx-manager?
    }
  }
}  
  
