# Change capture data mock/stub from account datastore

Creates the topics

```
./bin/kafka-console-consumer \
         --bootstrap-server localhost:9092 \
         --topic account \
         --property print.key=true \
         --from-beginning
```

