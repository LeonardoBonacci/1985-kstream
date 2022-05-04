## Activates accounts coming from another datastore

TODO same mechanism for pools

```
mvn clean install -pl :account-initializer

docker-compose build && docker-compose up -d && docker-compose logs -f account-initializer

docker stop account-initializer && docker rm account-initializer
```

