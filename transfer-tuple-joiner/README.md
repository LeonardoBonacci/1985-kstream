# Combines the previously split/tupled transfers


```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-eventual \
	--property parse.key=true \
 	--property key.separator=":"

abc:{"transferId":"abc","poolId": "coro", "from":"foo", "to":"bar", "amount":10.10, "when":1651078126344}


./kafka-console-consumer \
 --bootstrap-server localhost:9092 \
 --topic transfer-consistent \
 --property print.key=true \
--from-beginning
```


