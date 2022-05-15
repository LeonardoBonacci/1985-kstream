package guru.bonacci.heroes.accountinitializer;

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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.kafka.KafkaTopicNames;

@ExtendWith(SpringExtension.class)
public class AccountInitializerTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, AccountCDC> accountTopicIn;
  private TestOutputTopic<String, Account> accountTransferTopicOut;

  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    var app = new BootstrAppAccountInitializer();
    app.topology(builder);
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    accountTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.ACCOUNT_TOPIC, new StringSerializer(), new JsonSerializer<AccountCDC>());
    
    accountTransferTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC, new StringDeserializer(), new JsonDeserializer<Account>(Account.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @Test
  void shouldInsert() throws Exception {
    var cdc = AccountCDC.builder().poolId("foo").accountId("foo").accountName("foo").build();
    this.accountTopicIn.pipeInput(cdc.identifier(), cdc); // insert
    
    var expected = Account.builder().poolId(cdc.getPoolId()).accountId(cdc.getAccountId()).build();

    Thread.sleep(1000);
    
    var actual = accountTransferTopicOut.readValue();
    assertThat(actual.identifier()).isEqualTo(expected.identifier());
  }

  @Test
  void shouldNotUpdateInsert() throws Exception {
    var cdc = AccountCDC.builder().poolId("foo").accountId("foo").accountName("foo").build();
    this.accountTopicIn.pipeInput(cdc.identifier(), cdc); // insert
    
    Thread.sleep(1000);
    
    assertThat(accountTransferTopicOut.isEmpty()).isFalse();
    accountTransferTopicOut.readValue();

    Thread.sleep(1000);

    cdc.setAccountName("foo2");
    this.accountTopicIn.pipeInput(cdc.identifier(), cdc); // update
    
    assertThat(accountTransferTopicOut.isEmpty()).isTrue();
  }
}