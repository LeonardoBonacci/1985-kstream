# Heroes - Token exchange service is a platform that aims to aid local communities in experimenting with alternative forms of value exchange

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic accounts \
	--property parse.key=true \
 	--property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"a", "amount":100}]}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"b", "amount":100}]}


a:{"accountId":"a", "poolId":"coro", "transfers":[{"poolId": "coro", "from":"init", "to":"a", "amount":100}]}

./bin/kafka-console-consumer \
 --bootstrap-server localhost:9092 \
 --topic accounts \
 --from-beginning

curl -d '{"poolId": "sardex", "from":"b", "to":"a", "amount":10.10}' -H "Content-Type: application/json" -X POST localhost:8080
```

