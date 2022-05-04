# Splits transfers in a from and a to - to be processed and joined again later

```
mvn clean install -pl :transfer-tupler

docker-compose build && docker-compose up -d && docker-compose logs -f transfer-tupler

docker stop transfer-tupler && docker rm transfer-tupler
```


```
{"poolId":"sardex","from":"b","to":"a","amount":10.1,"when":1651078126344}
```

