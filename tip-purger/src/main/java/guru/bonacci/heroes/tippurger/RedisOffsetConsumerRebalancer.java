package guru.bonacci.heroes.tippurger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//TODO @@Component
@RequiredArgsConstructor
public class RedisOffsetConsumerRebalancer implements ConsumerAwareRebalanceListener {

  private final KafkaOffsetCache offsets;
  private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
  
  public void addOffsetToTrack(String topic, int partition, long offset){
    addOffsetToTrack(
            new TopicPartition(topic, partition),
            new OffsetAndMetadata(offset + 1, null));
  }

  public void addOffsetToTrack(TopicPartition topicPartition, OffsetAndMetadata offsetMetadata){
    currentOffsets.put(topicPartition, offsetMetadata);
  }

  public OffsetAndMetadata getCurrentOffsets(TopicPartition topicPartition) {
      return currentOffsets.get(topicPartition);
  }
  
  @Override
  public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    log.info("onPartitionsRevoked callback triggered");
    log.info("Committing offsets: " + currentOffsets);
    
    partitions.forEach(p -> {
      long offset = currentOffsets.get(p).offset();
      offsets.save(new KafkaOffset(KafkaOffset.key(p.topic(), p.partition()), offset));
    });  
  }

  @Override
  public void onPartitionsAssigned(Consumer<?,?> consumer, Collection<TopicPartition> partitions) {
    partitions.forEach(p -> {
      long offset = offsets.findById(KafkaOffset.key(p.topic(), p.partition())).get().getOffset(); //TODO
      consumer.seek(p, offset);
    });
  }
}
