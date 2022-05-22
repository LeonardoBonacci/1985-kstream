package guru.bonacci.heroes.accounttransfer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_COMPRESSED_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PAIR_TOPIC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.google.common.collect.Lists;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class AppAccountTransfer {

  public static void main(String[] args) {
		SpringApplication.run(AppAccountTransfer.class, args);
	}

	
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder, @Value("${max.number.of.transfers.in.account:100}") Integer maxSize) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> transferStream = // key: poolId.from or poolId.to
      builder
       .stream(TRANSFER_PAIR_TOPIC, Consumed.with(Serdes.String(), transferSerde))
       .peek((poolAccountId, transfer) -> log.info("in transfer {}<>{}", poolAccountId, transfer));

    KTable<String, Account> accountTable = // key: poolId.accountId
      builder
       .table(ACCOUNT_TRANSFER_TOPIC, Consumed.with(Serdes.String(), accountSerde));

    KStream<String, Account> accountTransferStream = 
      transferStream 
        .join(accountTable, (transfer, account) -> account.addTransfer(transfer))
        .peek((poolAccountId, account) -> log.info("joined account {}<>{}", poolAccountId, account));

    KStream<String, CompressedAccount> compressedStream =
      accountTransferStream.mapValues((poolAccountId, account) -> compressTransfers(account, maxSize));
    
    new KafkaStreamBrancher<String, CompressedAccount>()
      .branch((poolAccountId, compressed) -> !compressed.getAffectedTransfers().isEmpty(), affectedStream -> affectedStream
          .flatMapValues((poolAccountId, compressed) -> compressed.getAffectedTransfers())
          .to(TRANSFER_COMPRESSED_TOPIC, Produced.with(Serdes.String(), transferSerde)))
      .onTopOf(compressedStream)
      .mapValues((poolAccountId, compressed) -> compressed.getAccount())
      .peek((poolAccountId, account) -> log.info("out account {}<>{} with last transfer {} ", poolAccountId, account, account.latestTransfer()))
      .to(ACCOUNT_TRANSFER_TOPIC, Produced.with(Serdes.String(), accountSerde));
    
    return transferStream;
  }
  
  
  private CompressedAccount compressTransfers(Account account, int maxSize) {
    if (account.getTransfers().size() <= maxSize) {
      return new CompressedAccount(account, Collections.emptyList());
    }
    
    // this algorithm only works for max-size > 2
    var allTransfers = Lists.partition(account.getTransfers(), maxSize - 1);

    var newTransfers = new ArrayList<>(allTransfers.get(0)); // untouched
    var transfersToCompress = new ArrayList<>(allTransfers.get(1)); 
    var compressedTransfer = compress(transfersToCompress);
    log.info("compressing multiple transfers {} into one {}", transfersToCompress, compressedTransfer);
    newTransfers.add(compressedTransfer);
    
    account.setTransfers(newTransfers);
    return new CompressedAccount(account, transfersToCompress);
  }

  private Transfer compress(List<Transfer> transfers) {
    var newAmount = transfers.stream()
                      .map(tf -> tf.getAmount())
                      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new Transfer(UUID.randomUUID().toString(), 
                        transfers.get(0).getPoolId(),
                        "compressed",
                        "compressed",
                        newAmount,
                        System.currentTimeMillis());
  }

  public BigDecimal determineBalance(Account account) {
    return account.getTransfers().stream()
                    .map(tf -> tf.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class CompressedAccount {
    private Account account;
    private List<Transfer> affectedTransfers;
  }
  
}
