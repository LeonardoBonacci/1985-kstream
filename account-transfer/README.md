# Transfer processing service

```
./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic account-transfer \
	--property parse.key=true \
 	--property key.separator=":"

 private String transferId; //required
  private String poolId; //required
  private String from; //required
  private String to; //required
  private BigDecimal amount; //required
  private long when; //required
  
  
coro.a:{"accountId":"a", "poolId":"coro", "transfers":[], "balance": 0}
coro.k:{"accountId":"g", "poolId":"coro", "transfers":[], "balance": 0}


./bin/kafka-console-producer \
	--bootstrap-server localhost:9092 \
	--topic transfer-pair \
	--property parse.key=true \
 	--property key.separator=":"

coro.k:{"transferId":"abc1","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
coro.k:{"transferId":"abc2","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
coro.k:{"transferId":"abc3","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
coro.k:{"transferId":"abc4","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
coro.k:{"transferId":"abc5","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
coro.k:{"transferId":"abc6","poolId": "coro", "from":"d", "to":"b", "amount":-1.00, "when":1651078126344}
```

