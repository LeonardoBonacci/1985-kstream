package guru.bonacci.heroes.accounttransfers;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNTS_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC;

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
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.AccountCDC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppAccountTransfersInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountTransfersInitializer.class, args);
	}

	@Bean
	public KStream<String, AccountCDC> topology(StreamsBuilder builder) {
	  final var accountCDCSerde = new JsonSerde<AccountCDC>(AccountCDC.class);
	  final var accountSerde = new JsonSerde<Account>(Account.class);

	  KStream<String, AccountCDC> accountStream = // key: poolId.accountId
	      builder.stream(ACCOUNTS_TOPIC, Consumed.with(Serdes.String(), accountCDCSerde));

	  KTable<String, Account> accountTransferTable = // key: poolId.accountId
	      builder.table(ACCOUNT_TRANSFERS_TOPIC, Consumed.with(Serdes.String(), accountSerde));
	  
    accountStream
      .leftJoin(accountTransferTable, new AccountJoiner())
      .filter((identifier, wrapper) -> wrapper.isInsert())
      .mapValues(AccountWrapper::getAccount)
      .peek((identifier, account) -> log.info("inserting {} <> {}", identifier, account))
      .to(ACCOUNT_TRANSFERS_TOPIC, Produced.with(Serdes.String(), accountSerde));
  	return accountStream;
	}
	
	static class AccountJoiner implements ValueJoiner<AccountCDC, Account, AccountWrapper> {

	  public AccountWrapper apply(AccountCDC cdc, Account account) {
	    return account != null 
	       ? new AccountWrapper(false, account)
	       : new AccountWrapper(true, Account.builder().poolId(cdc.getPoolId()).accountId(cdc.getAccountId()).build());
	  }
	}
}
