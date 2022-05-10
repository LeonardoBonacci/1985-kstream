# Combines the previously created transfer-pair

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer \
	--property parse.key=true \
 	--property key.separator=":"

coro.foo:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}

aaaaa:{"transferId":"asdsad","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}



./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-consistent \
	--property parse.key=true \
 	--property key.separator=":"

abc:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}




```