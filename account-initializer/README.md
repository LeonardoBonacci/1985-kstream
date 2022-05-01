# Inserts new accounts


```
mvn clean install -pl :account-initializer

docker-compose build && docker-compose up -d && docker-compose logs -f account-initializer

docker stop account-initializer && docker rm account-initializer
```

