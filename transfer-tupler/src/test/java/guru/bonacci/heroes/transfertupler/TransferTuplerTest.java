package guru.bonacci.heroes.transfertupler;

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
public class TransferTuplerTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, Transfer> transfersTopicIn;
  private TestOutputTopic<String, Transfer> transferTuplesTopicOut;

  @BeforeEach
  void init() throws Exception {
    var builder = new StreamsBuilder();
    
    var app = new BootstrAppTransferTupler();
    app.topology(builder);
    var topology = builder.build();

    var props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde.class);

    testDriver = new TopologyTestDriver(topology, props);

    transfersTopicIn = 
        testDriver.createInputTopic(KafkaTopicNames.TRANSFERS_TOPIC, new StringSerializer(), new JsonSerializer<Transfer>());

    transferTuplesTopicOut = 
        testDriver.createOutputTopic(KafkaTopicNames.TRANSFER_TUPLES_TOPIC, new StringDeserializer(), new JsonDeserializer<Transfer>(Transfer.class));
  }
  
  @AfterEach
  void down() {
    testDriver.close();
  }

  @Test
  void shouldWork() throws Exception {
    var transfer = Transfer.builder().poolId("foo").from("a").to("b").amount(BigDecimal.TEN).transferId("none").when(42l).build();

    this.transfersTopicIn.pipeInput(Account.identifier(transfer.getPoolId(), transfer.getFrom()), transfer);
    
    Thread.sleep(1000);
    
    var from = transferTuplesTopicOut.readKeyValue();
    assertThat(from.value).isNotEqualTo(transfer);
    assertThat(from.key).isEqualTo(Account.identifier(transfer.getPoolId(), transfer.getFrom()));
    assertThat(from.value.getAmount()).isLessThan(BigDecimal.ZERO);

    var to = transferTuplesTopicOut.readKeyValue();
    assertThat(to.value).isEqualTo(transfer);
    assertThat(to.key).isEqualTo(Account.identifier(transfer.getPoolId(), transfer.getTo()));
    assertThat(transferTuplesTopicOut.isEmpty()).isTrue();
    assertThat(to.value.getAmount()).isGreaterThan(BigDecimal.ZERO);
  }
}