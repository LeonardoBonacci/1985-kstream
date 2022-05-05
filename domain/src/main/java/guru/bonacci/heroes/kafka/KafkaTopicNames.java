package guru.bonacci.heroes.kafka;

public class KafkaTopicNames {

  public static final String ACCOUNTS_TOPIC = "accounts"; // key: poolId.accountId
  public static final String ACCOUNT_TRANSFERS_TOPIC = "account-transfers"; // key: poolId.accountId
  public static final String ACCOUNT_STORAGE_SINK_TOPIC = "account-storage-sink"; // key: poolId.accountId
  
  public static final String TRANSFER_VALIDATION_REQUEST_TOPIC = "transfer-validation-requests"; // key: poolId.from
  public static final String TRANSFER_VALIDATION_REPLIES_TOPIC = "transfer-validation-replies"; // key: poolId.from

  public static final String TRANSFERS_TOPIC = "transfers"; // key: poolId.from
  public static final String TRANSFER_TUPLES_TOPIC = "transfer-tuples"; // key: poolId.from / poolId.to
  public static final String TRANSFER_EVENTUAL_TOPIC = "transfer-eventual"; // key: transferId
  public static final String TRANSFER_CONSISTENT_TOPIC = "transfer-consistent"; // key: transferId
  public static final String TRANSFER_HOUSTON_TOPIC = "transfer-houston"; // key: transferId
}
