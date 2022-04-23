# heroes

```
curl localhost:8080/accounts/a/heroes

curl localhost:8080/accounts/balance/a/heroes

curl -d '{"from":"b", "to":"a", "amount":10.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers/heroes
curl -d '{"from":"b", "to":"a", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers/heroes

```