package guru.bonacci.heroes.accountstorage;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_STORAGE_SINK_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_VALIDATION_REPLIES_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_VALIDATION_REQUEST_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.accountstorage.service.TransferValidationService;
import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import lombok.RequiredArgsConstructor;

@EnableKafkaStreams
@SpringBootApplication
@RequiredArgsConstructor
public class BootstrAppAccountStorage {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountStorage.class, args);
	}

	private final TransferValidationService validator;
	
	
  @Bean
  public KStream<String, TransferValidationRequest> topology(StreamsBuilder builder) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferValidationRequestSerde = new JsonSerde<TransferValidationRequest>(TransferValidationRequest.class);
    final var transferValidationResponseSerde = new JsonSerde<TransferValidationResponse>(TransferValidationResponse.class);
    
    KStream<String, TransferValidationRequest> requestStream = // key: poolId.from
      builder
      .stream(TRANSFER_VALIDATION_REQUEST_TOPIC, Consumed.with(Serdes.String(), transferValidationRequestSerde));

    KTable<String, Account> accountTable = // key: poolId.accountId
      builder.table(ACCOUNT_STORAGE_SINK_TOPIC, Consumed.with(Serdes.String(), accountSerde));
    accountTable
      .toStream()
      .print(Printed.toSysOut());
    
    requestStream 
      .leftJoin(accountTable, (request, account) -> validator.getTransferValidationInfo(request, account))
      .to(TRANSFER_VALIDATION_REPLIES_TOPIC, Produced.with(Serdes.String(), transferValidationResponseSerde));
    return requestStream;
  }
}
