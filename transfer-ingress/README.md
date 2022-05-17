# Transfer entry point - Rest Endpoint

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
	 --topic transfer \
	 --from-beginning
```

### k8s
 
```
kubectl port-forward service/transfer-ingress-service 8080:8080 -n kafka
```