# Heroes - Token exchange service is a platform that aims to aid local communities in experimenting with alternative forms of value exchange


```
mvn clean install -pl :transfer-ingress

docker-compose build && docker-compose up -d && docker-compose logs -f transfer-ingress

docker stop transfer-ingress && docker rm transfer-ingress
```

### coro
* foo
* goo
* bar
* baz

```
valid
curl -d '{"poolId": "coro", "from":"foo", "to":"bar", "amount":10.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers
curl -d '{"poolId": "coro", "from":"baz", "to":"goo", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers

invalid
curl -d '{"from":"b", "to":"a", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers
curl -d '{"poolId": "coro", "from":"xxx", "to":"goo", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers
curl -d '{"poolId": "coro", "from":"bar", "to":"xxx", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers
curl -d '{"poolId": "coro", "from":"bar", "to":"goo", "amount":9999.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers
```

```
./bin/kafka-console-consumer \
	 --bootstrap-server localhost:9092 \
	 --topic transfers \
	 --from-beginning
 ```
