package guru.bonacci.heroes.transfertuplejoiner;

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
public class TransferTupleJoinerTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, Transfer> transferEventualTopicIn;

  private TestOutputTopic<String, Transfer> transferConsistentTopicOut;

  
  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    
    var app = new BootstrAppTransferTupleJoiner();
    app.topology(builder, 1l); // 1 sec window
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    transferEventualTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC, new StringSerializer(), new JsonSerializer<Transfer>());

    transferConsistentTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.TRANSFER_CONSISTENT_TOPIC, new StringDeserializer(), new JsonDeserializer<Transfer>(Transfer.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @Test
  void shouldWork() throws Exception {
    var transfer = Transfer.builder().transferId("foobar").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).when(System.currentTimeMillis()).build();
    this.transferEventualTopicIn.pipeInput(Account.identifier(transfer.getPoolId(), transfer.getFrom()), transfer);
    this.transferEventualTopicIn.pipeInput(Account.identifier(transfer.getPoolId(), transfer.getTo()), transfer);
    
    Thread.sleep(3000);
    
    var result = transferConsistentTopicOut.readValue();
    assertThat(result).isEqualTo(transfer);
//    assertThat(transferConsistentTopicOut.isEmpty()).isTrue();

  }
}