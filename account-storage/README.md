# Account admin service


```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account-storage-sink \
	--property parse.key=true \
 	--property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[]}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[]}

./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-validation-requests \
	--property parse.key=true \
 	--property key.separator=":"

coro.a:{"from":"a", "poolId":"coro", "to":"b"}
coro.a:{"from":"a", "poolId":"coro", "to":"c"}

./bin/kafka-console-consumer \
 --bootstrap-server localhost:9092 \
 --topic transfer-validation-replies \
 --from-beginning

```
