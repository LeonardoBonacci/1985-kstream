package guru.bonacci.heroes.tippurger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaOffset {

  private String topicPartition; // topic.partition (String + int)
  private Long offset;
  
  
  public static String key(String topic, Integer partition) {
    return topic + "." + partition;
  }
}
