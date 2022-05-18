package guru.bonacci.heroes.kafka;

public class KafkaTopicNames {

  public static final String ACCOUNT_TOPIC = "account"; // key: poolId.accountId
  public static final String ACCOUNT_TRANSFER_TOPIC = "account-transfer"; // key: poolId.accountId
  
  public static final String TRANSFER_VALIDATION_REQUEST_TOPIC = "transfer-validation-request"; // key: poolId.from
  public static final String TRANSFER_VALIDATION_RESPONSE_TOPIC = "transfer-validation-response"; // key: poolId.from

  public static final String TRANSFER_TOPIC = "transfer"; // key: poolId.from
  public static final String TRANSFER_PAIR_TOPIC = "transfer-pair"; // key: poolId.from / poolId.to
  public static final String TRANSFER_PROCESSED_TOPIC = "transfer-processed"; // key: transferId

  public static final String TRANSFER_HOUSTON_TOPIC = "transfer-houston"; // key: transferId
}
