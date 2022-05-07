package guru.bonacci.heroes.accounttransfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;

@ExtendWith(SpringExtension.class)
public class AccountTransferTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, Transfer> transferTuplesTopicIn;
  private TestInputTopic<String, Account> accountTransfersTopicIn;

  private TestOutputTopic<String, Account> accountTransferTopicOut;
  private TestOutputTopic<String, Account> accountStorageSinkTopicOut;
  private TestOutputTopic<String, Transfer> transferEventualTopicOut;

  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    var app = new BootstrAppAccountTransfer();
    app.topology(builder);
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    transferTuplesTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.TRANSFER_TUPLES_TOPIC, new StringSerializer(), new JsonSerializer<Transfer>());

    accountTransfersTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC, new StringSerializer(), new JsonSerializer<Account>());

    accountTransferTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC, new StringDeserializer(), new JsonDeserializer<Account>(Account.class));

    accountStorageSinkTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.ACCOUNT_STORAGE_SINK_TOPIC, new StringDeserializer(), new JsonDeserializer<Account>(Account.class));

    transferEventualTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC, new StringDeserializer(), new JsonDeserializer<Transfer>(Transfer.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @RepeatedTest(100)
  void shouldWork(TestInfo info) throws Exception {
    var accountWrite = Account.builder().poolId("foo").accountId("foo").build();
    this.accountTransfersTopicIn.pipeInput(accountWrite.identifier(), accountWrite);
    
    Thread.sleep(1000);

    var sink1 = accountStorageSinkTopicOut.readValue();
    assertThat(sink1.identifier()).isEqualTo(accountWrite.identifier());
    assertThat(sink1.getTransfers().size()).isEqualTo(0);

    assertThat(transferEventualTopicOut.isEmpty()).isTrue();

    var transferWrite = Transfer.builder().transferId("tid").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).build();
    this.transferTuplesTopicIn.pipeInput(transferWrite.poolAccountId(), transferWrite);
    
    Thread.sleep(1000);
    
    var accountRead = accountTransferTopicOut.readValue();
    assertThat(accountRead.identifier()).isEqualTo(accountWrite.identifier());
    assertThat(accountRead.getTransfers().size()).isEqualTo(1);
    assertThat(accountRead.latestTransfer()).isEqualTo(transferWrite);

    var sink2 = accountStorageSinkTopicOut.readValue();
    assertThat(sink2.identifier()).isEqualTo(accountWrite.identifier());
    assertThat(sink2.getTransfers().size()).isEqualTo(1);

    var transferRead = transferEventualTopicOut.readValue();
    assertThat(transferRead).isEqualTo(transferWrite);

    var transfer2Write = Transfer.builder().transferId("tid").poolId("foo").from("foo").to("baz").amount(BigDecimal.ONE).build();
    this.transferTuplesTopicIn.pipeInput(transfer2Write.poolAccountId(), transfer2Write);
    
    Thread.sleep(1000);
    
    var account2Read = accountTransferTopicOut.readValue();
    assertThat(account2Read.identifier()).isEqualTo(accountWrite.identifier());
    assertThat(account2Read.getTransfers().size()).isEqualTo(2);
    assertThat(account2Read.latestTransfer()).isEqualTo(transfer2Write);
    
    var sink3 = accountStorageSinkTopicOut.readValue();
    assertThat(sink3.identifier()).isEqualTo(accountWrite.identifier());
    assertThat(sink3.getTransfers().size()).isEqualTo(2);
    
    var transfer2Read = transferEventualTopicOut.readValue();
    assertThat(transfer2Read).isEqualTo(transfer2Write);

  }
}