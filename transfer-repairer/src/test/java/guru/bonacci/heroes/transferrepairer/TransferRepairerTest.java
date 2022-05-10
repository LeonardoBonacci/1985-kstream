package guru.bonacci.heroes.transferrepairer;

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

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import guru.bonacci.heroes.transferrepairer.BootstrAppTransferRepairer;

@ExtendWith(SpringExtension.class)
public class TransferRepairerTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, Transfer> transferEventualTopicIn;
  private TestOutputTopic<String, Transfer> transferConsistentTopicOut;

  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    var app = new BootstrAppTransferRepairer();
    app.topology(builder);
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

  @RepeatedTest(10)
  void shouldWorkPositiveFirstFast(TestInfo test) throws Exception {
    var now = System.currentTimeMillis();
    var tpos = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).when(now).build();
    var tneg = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN.negate()).when(now).build();

    this.transferEventualTopicIn.pipeInput(tpos.getTransferId(), tpos); 
    this.transferEventualTopicIn.pipeInput(tneg.getTransferId(), tneg); 

    Thread.sleep(1000);

    var actual = transferConsistentTopicOut.readValue();
    assertThat(actual).isEqualTo(tpos);

    assertThat(transferConsistentTopicOut.isEmpty()).isTrue();
  }
  
  @RepeatedTest(10)
  void shouldWorkPositiveFirstSlow(TestInfo test) throws Exception {
    var now = System.currentTimeMillis();
    var tpos = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).when(now).build();
    var tneg = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN.negate()).when(now).build();

    this.transferEventualTopicIn.pipeInput(tpos.getTransferId(), tpos); 
    Thread.sleep(3000);
    this.transferEventualTopicIn.pipeInput(tneg.getTransferId(), tneg); 

    Thread.sleep(1000);

    var actual = transferConsistentTopicOut.readValue();
    assertThat(actual).isEqualTo(tpos);

    assertThat(transferConsistentTopicOut.isEmpty()).isTrue();
  }

  @RepeatedTest(10)
  void shouldWorkPositiveNegativeFast(TestInfo test) throws Exception {
    var now = System.currentTimeMillis();
    var tpos = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).when(now).build();
    var tneg = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN.negate()).when(now).build();

    this.transferEventualTopicIn.pipeInput(tneg.getTransferId(), tneg); 
    this.transferEventualTopicIn.pipeInput(tpos.getTransferId(), tpos); 

    Thread.sleep(1000);

    var actual = transferConsistentTopicOut.readValue();
    assertThat(actual).isEqualTo(tpos);

    assertThat(transferConsistentTopicOut.isEmpty()).isTrue();
  }
  
  @RepeatedTest(10)
  void shouldWorkNegativeFirstSlow(TestInfo test) throws Exception {
    var now = System.currentTimeMillis();
    var tpos = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN).when(now).build();
    var tneg = Transfer.builder().transferId("abc").poolId("foo").from("foo").to("bar").amount(BigDecimal.TEN.negate()).when(now).build();

    this.transferEventualTopicIn.pipeInput(tneg.getTransferId(), tneg); 
    Thread.sleep(3000);
    this.transferEventualTopicIn.pipeInput(tpos.getTransferId(), tpos); 

    Thread.sleep(1000);

    var actual = transferConsistentTopicOut.readValue();
    assertThat(actual).isEqualTo(tpos);

    assertThat(transferConsistentTopicOut.isEmpty()).isTrue();
  }
}