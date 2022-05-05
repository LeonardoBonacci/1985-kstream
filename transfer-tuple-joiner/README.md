# Combines the previously split/tupled transfers


```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-eventual \
	--property parse.key=true \
 	--property key.separator=":"

bzw:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
bb:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
bzw:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
```

