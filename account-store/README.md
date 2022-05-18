# Account Restful service

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account-transfer \
	--property parse.key=true \
 	--property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[],"balance":0}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[],"balance":0}
```

```
curl localhost:8080/metadata
curl localhost:8080/metadata/accounts
curl localhost:8080/metadata/pools/coro/accounts/a

curl localhost:8080/pools/coro/accounts/a
curl localhost:8080/pools/coro/accounts/a/balance
```

### local

```
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8085'
```

### docker

```
docker-compose -f docker-compose.yml -f docker-compose-apps.yml build account-store
docker-compose -f docker-compose.yml -f docker-compose-apps.yml up -d account-store
docker-compose -f docker-compose.yml -f docker-compose-apps.yml scale account-store=2
```

### k8s

```
gcloud container clusters resize hello-cluster --node-pool default-pool --num-nodes 3 --zone us-central1-a

kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl apply -f k8s/infra -n kafka
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka 

kubectl apply -f k8s/topics -n kafka
kubectl apply -f k8s/apps -n kafka

kubectl config set-context --current --namespace=kafka
kubectl logs -f deploy/simulator-app

kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.28.0-kafka-3.1.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic account-transfer --property parse.key=true --property key.separator=":"

kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.28.0-kafka-3.1.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic transfer --from-beginning

kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.28.0-kafka-3.1.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --property print.timestamp=true --property print.key=true --value-deserializer=org.apache.kafka.common.serialization.LongDeserializer --topic transfer-houston --from-beginning

 


coro.a:{"accountId":"a", "poolId":"coro", "transfers":[], "balance" = 0.0}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[], "balance" = 0.0}

https://kubernetes.io/docs/tasks/access-application-cluster/port-forward-access-application-cluster/

kubectl port-forward service/account-store-service 8080:8080
kubectl port-forward pod/account-store-app-5d5665d55c-56m79 -n kafka 8080:8080


```


## validaton

```
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



```
https://strimzi.io/quickstarts/

kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl apply -f  kafka-persistent-single.yaml

kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.28.0-kafka-3.1.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic account-transfer --property parse.key=true --property key.separator=":"

coro.a:{"accountId":"a", "poolId":"coro", "transfers":[], "balance" = 0.0}
coro.b:{"accountId":"b", "poolId":"coro", "transfers":[], "balance" = 0.0}

https://kubernetes.io/docs/tasks/access-application-cluster/port-forward-access-application-cluster/

kubectl port-forward service/account-store-service 8080:8080

kubectl port-forward pods/account-store-app-5d5665d55c-czq9h -n kafka 8080:8080

```

