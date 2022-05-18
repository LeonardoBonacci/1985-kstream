# Sends a transfer every second and prints some insights

```
./bin/kafka-console-consumer \
         --bootstrap-server localhost:9092 \
         --topic transfer-processed \
         --property print.key=true \
         --from-beginning
```

