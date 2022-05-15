# Monitors that each side of the transfer-split is processed within n seconds

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-eventual \
	--property parse.key=true \
 	--property key.separator=":"

bzw:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
bz:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}
z:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}

aaa:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
bbb:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
ccc:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
ddd:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
eee:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":-10.10, "when":1651078126344}
```