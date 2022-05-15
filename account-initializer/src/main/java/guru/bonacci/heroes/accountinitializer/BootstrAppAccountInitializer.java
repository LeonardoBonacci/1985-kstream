package guru.bonacci.heroes.accountinitializer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.AccountCDC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class BootstrAppAccountInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountInitializer.class, args);
	}

	@Bean
	public KStream<String, AccountCDC> topology(StreamsBuilder builder) {
	  final var accountCDCSerde = new JsonSerde<AccountCDC>(AccountCDC.class);
	  final var accountSerde = new JsonSerde<Account>(Account.class);

	  KStream<String, AccountCDC> accountStream = // key: poolId.accountId
	    builder
	      .stream(ACCOUNT_TOPIC, Consumed.with(Serdes.String(), accountCDCSerde))
	      .peek((k,v) -> log.info("in {}<>{}", k, v));

	  KTable<String, Account> accountTransferTable = // key: poolId.accountId
	    builder
	      .table(ACCOUNT_TRANSFER_TOPIC, Consumed.with(Serdes.String(), accountSerde));
	  
    accountStream
      .leftJoin(accountTransferTable, new AccountJoiner())
      .filter((identifier, wrapper) -> wrapper.isInsert())
      .mapValues(AccountUpsert::getAccount)
      .peek((poolAccountId, account) -> log.info("out {} <> {}", poolAccountId, account))
      .to(ACCOUNT_TRANSFER_TOPIC, Produced.with(Serdes.String(), accountSerde));

    return accountStream;
	}
	
	static class AccountJoiner implements ValueJoiner<AccountCDC, Account, AccountUpsert> {

	  public AccountUpsert apply(AccountCDC cdc, Account account) {
	    return account != null 
	       ? new AccountUpsert(false, account)
	       : new AccountUpsert(true, Account.builder().poolId(cdc.getPoolId()).accountId(cdc.getAccountId()).build());
	  }
	}
}
