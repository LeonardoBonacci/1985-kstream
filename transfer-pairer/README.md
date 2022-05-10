# Splits transfers in a from and a to - to be processed and joined again later

```
mvn clean install -pl :transfer-pairer

docker-compose build && docker-compose up -d && docker-compose logs -f transfer-pairer

docker stop transfer-pairer && docker rm transfer-pairer
```


```
{"poolId":"sardex","from":"b","to":"a","amount":10.1,"when":1651078126344}
```

