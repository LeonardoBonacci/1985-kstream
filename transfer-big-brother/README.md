# Combines the previously split/tupled transfers


TODO

stream-stream join eventual with consistent - consistent being 42 seconds later -> if no result: houston
groupby count -> if > 2: houston 

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-eventual \
	--property parse.key=true \
 	--property key.separator=":"

bzw:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
```

./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic foo \
	--property parse.key=true \
 	--property key.separator=":"
 	