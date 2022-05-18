package guru.bonacci.heroes.accountstore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import guru.bonacci.heroes.accountstore.AppAccountStore;
import guru.bonacci.heroes.accountstore.validation.TransferValidationService;
import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import guru.bonacci.heroes.kafka.KafkaTopicNames;

@ExtendWith(SpringExtension.class)
public class AccountStoreTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, TransferValidationRequest> transferValidationRequestsTopicIn;
  private TestInputTopic<String, Account> accountTransferTopicIn;

  private TestOutputTopic<String, TransferValidationResponse> transferValidationRepliesTopicOut;

  private TransferValidationService validator;
  
  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    
    validator = Mockito.mock(TransferValidationService.class);
    var app = new AppAccountStore(validator);
    app.topology(builder);
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    transferValidationRequestsTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.TRANSFER_VALIDATION_REQUEST_TOPIC, new StringSerializer(), new JsonSerializer<TransferValidationRequest>());

    accountTransferTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC, new StringSerializer(), new JsonSerializer<Account>());

    transferValidationRepliesTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.TRANSFER_VALIDATION_RESPONSE_TOPIC, new StringDeserializer(), new JsonDeserializer<TransferValidationResponse>(TransferValidationResponse.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @Test
  void shouldWork() throws Exception {
    var account = Account.builder().poolId("foo").accountId("foo").build();
    var expected = new TransferValidationResponse(true, true, true, account, null);
    Mockito.when(validator.getTransferValidationInfo(Mockito.any(TransferValidationRequest.class), Mockito.any(Account.class)))
           .thenReturn(expected);
    
    this.accountTransferTopicIn.pipeInput(account.identifier(), account);
    
    Thread.sleep(1000);
    
    var request = new TransferValidationRequest("foo", "foo", "bar");
    this.transferValidationRequestsTopicIn.pipeInput(Account.identifier(request.getPoolId(), request.getFrom()), request);
    
    Thread.sleep(1000);
    
    var actual = transferValidationRepliesTopicOut.readValue();
    assertThat(actual).isEqualTo(expected);
  }
}