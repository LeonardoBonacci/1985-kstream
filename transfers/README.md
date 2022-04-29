coro.b:{"accountId":"b", "poolId":"coro", "transfers":[]}# Heroes - Token exchange service is a platform that aims to aid local communities in experimenting with alternative forms of value exchange

```

curl -d '{"poolId": "coro", "from":"b", "to":"a", "amount":10.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers

no pool - invalid
curl -d '{"from":"b", "to":"a", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers

```