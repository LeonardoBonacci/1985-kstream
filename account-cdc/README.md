# Change capture data mock/stub from account datastore

Also creates all the topics

```
./bin/kafka-console-consumer \
         --bootstrap-server localhost:9092 \
         --topic accounts \
         --property print.key=true \
         --from-beginning
```

