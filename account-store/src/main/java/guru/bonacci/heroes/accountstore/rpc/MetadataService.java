package guru.bonacci.heroes.accountstore.rpc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetadataService {

  private final KafkaStreamsService streams;

  
  public List<HostStoreInfo> streamsMetadata() {
    final Collection<StreamsMetadata> metadata = streams.getKafkaStreams().allMetadata();
    return mapInstancesToHostStoreInfo(metadata);
  }

  public List<HostStoreInfo> streamsMetadataForStore(final  String store) {
    final Collection<StreamsMetadata> metadata = streams.getKafkaStreams().allMetadataForStore(store);
    return mapInstancesToHostStoreInfo(metadata);
  }

  public <K> HostStoreInfo streamsMetadataForStoreAndKey(final String store,
                                                         final K key,
                                                         final Serializer<K> serializer) {
    final KeyQueryMetadata metadata = streams.getKafkaStreams().queryMetadataForKey(store, key, serializer);
    if (metadata == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
    }

    return new HostStoreInfo(metadata.activeHost().host(),
                             metadata.activeHost().port(),
                             Collections.singleton(store));
  }

  private List<HostStoreInfo> mapInstancesToHostStoreInfo(
      final Collection<StreamsMetadata> metadatas) {
    return metadatas.stream().map(metadata -> new HostStoreInfo(metadata.host(),
                                                                metadata.port(),
                                                                metadata.stateStoreNames()))
        .collect(Collectors.toList());
  }
}
