# Transfer processing service

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account \
	--property parse.key=true \
 	--property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"a", "amount":100}], "balance" = 100}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"b", "amount":100}], "balance" = 100}


a:{"accountId":"a", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"a", "amount":100}], "balance" = 100}

./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account \
	--property parse.key=true \
 	--property key.separator=":"

coro.foo:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
coro.bar:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
```

