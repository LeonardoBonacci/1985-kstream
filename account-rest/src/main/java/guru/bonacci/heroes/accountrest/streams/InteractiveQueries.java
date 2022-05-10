package guru.bonacci.heroes.accountrest.streams;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.jboss.logging.Logger;
import org.wildfly.common.net.HostName;

import guru.bonacci.heroes.accountrest.model.AccountData;
import guru.bonacci.heroes.domain.Account;

@ApplicationScoped
public class InteractiveQueries {

    private static final Logger LOG = Logger.getLogger(InteractiveQueries.class);

    @Inject
    KafkaStreams streams;

    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(TopologyProducer.ACCOUNT_STORE)
                .stream()
                .map(m -> new PipelineMetadata(
                        m.hostInfo().host() + ":" + m.hostInfo().port(),
                        m.topicPartitions()
                                .stream()
                                .map(TopicPartition::toString)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public GetAccountDataResult getAccountData(String poolAccountId) {
        KeyQueryMetadata metadata = streams.queryMetadataForKey(
                TopologyProducer.ACCOUNT_STORE,
                poolAccountId,
                Serdes.String().serializer());

        if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
            LOG.warnv("Found no metadata for key {0}", poolAccountId);
            return GetAccountDataResult.notFound();
        } else if (metadata.activeHost().host().equals(HostName.getQualifiedHostName())) {
            LOG.infov("Found data for key {0} locally", poolAccountId);
            Account result = getAccountStore().get(poolAccountId);

            if (result != null) {
                return GetAccountDataResult.found(AccountData.from(result));
            } else {
                return GetAccountDataResult.notFound();
            }
        } else {
            LOG.infov("Found data for key {0} on remote host {1}:{2}", poolAccountId, metadata.activeHost().host(), metadata.activeHost().port());
            return GetAccountDataResult.foundRemotely(metadata.activeHost().host(), metadata.activeHost().port());
        }
    }

    private ReadOnlyKeyValueStore<String, Account> getAccountStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters.fromNameAndType(TopologyProducer.ACCOUNT_STORE, QueryableStoreTypes.keyValueStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
