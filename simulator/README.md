# Sends a transfer every second

```
./bin/kafka-console-consumer \
         --bootstrap-server localhost:9092 \
         --topic transfer-consistent \
         --property print.key=true \
         --from-beginning
```

