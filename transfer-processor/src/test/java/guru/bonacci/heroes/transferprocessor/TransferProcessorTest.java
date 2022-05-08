package guru.bonacci.heroes.transferprocessor;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;

@ExtendWith(SpringExtension.class)
public class TransferProcessorTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, Account> accountTransfersTopicIn;
  private TestOutputTopic<String, Transfer> transferEventualTopicOut;

  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    var app = new BootstrAppTransferProcessor();
    app.topology(builder);
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    accountTransfersTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC, new StringSerializer(), new JsonSerializer<Account>());

    transferEventualTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC, new StringDeserializer(), new JsonDeserializer<Transfer>(Transfer.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @Test
  void shouldPickLast() throws Exception {
    var account = Account.builder().poolId("foo").accountId("foo").build();
    var transfer = Transfer.builder().transferId("tid").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).build();
    account.addTransfer(transfer);
    var transfer2 = Transfer.builder().transferId("tid2").poolId("foo").from("foo").to("bar").amount(BigDecimal.ONE).build();
    account.addTransfer(transfer2);
    
    this.accountTransfersTopicIn.pipeInput(account.identifier(), account);
    
    Thread.sleep(1000);

    var transferProcessed = transferEventualTopicOut.readValue();
    assertThat(transferProcessed).isEqualTo(transfer2);
  }
  
  @Test
  void shouldPickNone() throws Exception {
    var account = Account.builder().poolId("foo").accountId("foo").build();
    
    this.accountTransfersTopicIn.pipeInput(account.identifier(), account);
    
    Thread.sleep(1000);

    assertThat(transferEventualTopicOut.isEmpty()).isTrue();
  }
}