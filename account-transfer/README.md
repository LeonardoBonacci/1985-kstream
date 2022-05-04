# Transfer processing service

```
mvn clean install -pl :account-transfer

docker-compose build && docker-compose up -d && docker-compose logs -f account-transfer

docker stop account-transfer && docker rm account-transfer
```

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

