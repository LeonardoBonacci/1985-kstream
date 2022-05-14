# Account admin service

Move this to a better place

```
https://strimzi.io/quickstarts/

kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl apply -f  kafka-persistent-single.yaml

kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.28.0-kafka-3.1.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic account-transfer --property parse.key=true --property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[]}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[]}

https://kubernetes.io/docs/tasks/access-application-cluster/port-forward-access-application-cluster/

kubectl port-forward service/account-store-service 8080:8080

kubectl port-forward pods/account-store-app-5d5665d55c-czq9h -n kafka 8080:8080

```



```
mvn clean install -pl :account-storage

docker-compose build && docker-compose up -d && docker-compose logs -f account-storage

docker stop account-storage && docker rm account-storage

docker-compose -f docker-compose.yml -f docker-compose-apps.yml up -d --scale  account-store=2
```

to run multiple services

```
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8085'


curl localhost:8080/state/instances/AccountStore
curl localhost:8080/state/keyvalue/AccountStore/all

curl localhost:8080/state/instances
curl localhost:8080/state/instances/AccountStore
curl localhost:8080/state/instance/AccountStore/coro.b
curl localhost:8080/state/keyvalue/coro.a

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
