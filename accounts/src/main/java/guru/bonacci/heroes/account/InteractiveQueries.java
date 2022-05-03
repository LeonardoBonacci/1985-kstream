package guru.bonacci.heroes.account;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractiveQueries {

    private static final QueryableStoreType<ReadOnlyKeyValueStore<String, Account>> ACCOUNT_STORE_TYPE = QueryableStoreTypes.keyValueStore();

    private final StreamsBuilderFactoryBean streamsBuilder;

    
    public List<PipelineMetadata> getMetaData() {
      final var streams = streamsBuilder.getKafkaStreams(); 
      return streams.allMetadataForStore(BootstrAppAccounts.ACCOUNTS_STORE_NAME)
              .stream()
              .map(m -> new PipelineMetadata(
                      m.hostInfo().host() + ":" + m.hostInfo().port(),
                      m.topicPartitions()
                              .stream()
                              .map(TopicPartition::toString)
                              .collect(Collectors.toSet())))
              .collect(Collectors.toList());
    }

    public GetTransferValidationResult getTransferValidation(String accountId) throws UnknownHostException {
      final var streams = streamsBuilder.getKafkaStreams(); 
      KeyQueryMetadata metadata = streams.queryMetadataForKey(
            BootstrAppAccounts.ACCOUNTS_STORE_NAME,
            accountId,
            Serdes.String().serializer());

      log.info("Found data for key {} locally", accountId);
      getAccountStore().all().forEachRemaining(System.out::println);
      Account a = getAccountStore().get(accountId);
      System.out.println(a);
      if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
          log.warn("Found no metadata for key {}", accountId);
          
          
          getAccountStore().all().forEachRemaining(System.out::println);
          return GetTransferValidationResult.notFound();
      } else 
        if (metadata.activeHost().host().equals(InetAddress.getLocalHost().getHostAddress())) {
          log.info("Found data for key {} locally", accountId);
          Account result = getAccountStore().get(accountId);

          if (result != null) {
              return GetTransferValidationResult.found(TransferValidationResult.from(result));
          } else {
              return GetTransferValidationResult.notFound();
          }
      } else {
          log.info("Found data for key {} on remote host {}:{}", accountId, metadata.activeHost().host(), metadata.activeHost().port());
          return GetTransferValidationResult.foundRemotely(metadata.activeHost().host(), metadata.activeHost().port());
      }
    }

    private ReadOnlyKeyValueStore<String, Account> getAccountStore() {
      final var streams = streamsBuilder.getKafkaStreams();
      while (true) {
          try {
            return streams.store(StoreQueryParameters.fromNameAndType(BootstrAppAccounts.ACCOUNTS_STORE_NAME, ACCOUNT_STORE_TYPE).enableStaleStores());
          } catch (InvalidStateStoreException e) {
              // ignore, store not ready yet
          }
      }
    }
 }