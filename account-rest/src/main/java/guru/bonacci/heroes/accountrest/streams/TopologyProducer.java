package guru.bonacci.heroes.accountrest.streams;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import guru.bonacci.heroes.domain.Account;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

@ApplicationScoped
public class TopologyProducer {

  static final String ACCOUNT_STORE = "account-store";


  @Produces
  public Topology buildTopology() {
    StreamsBuilder builder = new StreamsBuilder();

    ObjectMapperSerde<Account> accountSerde = new ObjectMapperSerde<>(Account.class);

    KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(ACCOUNT_STORE);

    builder // key: poolId.accountId
      .table(ACCOUNT_TRANSFER_TOPIC, 
          Consumed.with(Serdes.String(), accountSerde),
          Materialized.as(storeSupplier));

    return builder.build();
  }
}
