# Account admin service

```
mvn clean install -pl :account-storage

docker-compose build && docker-compose up -d && docker-compose logs -f account-storage

docker stop account-storage && docker rm account-storage
```

to run multiple services

```
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8085'


curl localhost:8080/state/instances/AccountStore
curl localhost:8080/state/keyvalue/AccountStore/all

curl localhost:8080/state/instances
curl localhost:8080/state/instances/AccountStore
curl localhost:8080/state/instance/AccountStore/coro.b
curl localhost:8080/state/keyvalue/AccountStore/coro.a

```

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account-transfer \
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
