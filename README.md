# Heroes - Anonymous token exchange service that aims to aid local communities in experimenting with alternative forms of value exchange



```
docker stop account-initializer && docker rm account-initializer
docker stop account-transfer && docker rm account-transfer
docker stop account-storage && docker rm account-storage
docker stop transfer-ingress && docker rm transfer-ingress
docker stop transfer-pairer && docker rm transfer-pairer
```


Community currencies need some more thought in practice

So why do they flop? Some don’t survive the loss of activist leadership. Others fail to scale for a different reason. “People don’t understand money,”

https://monneta.org/en/sardex/

Sardex - The credits are denominated in Euro but cannot be exchanged for Euro or bought with Euro

## TODO
implement sardex
exactly once semantics
k8s + scaling accounts
graalvm

## Basic concepts

an administrator creates an empty pool

there are different types of pools
1 sardex

TODO something about tokens?

the admin can invite members to the pool

in sardex, a pool-member can exchange tokens within the pool

each pool-member has a balance, represented in pool-tokens
pool-members can transfer tokens to each other - given that they have enough balance for the transfer
in Sardex the balance can be negative

there can be no cross-pool exchange

### The ordinary user

needs to install a phone-app

requests the gatekeeper to be added to a pool

acquiring tokens depends on the pool type - i.e. request the gatekeeper

uses the phone-app to transfer tokens to other members of the pool

### The gatekeeper

needs to install a phone-app

creates a pool - financial implications (payment depends on pool type)

allows or rejects people/members to its pool

depending on the pool-type, allows or rejects the acquisition of tokens by its pool-members

### The platform

the platform offers people/members a shielded realm to exchange tokens with other members in the form of pools

different types of pools maintain different member-entrance and token-acquisition policies - with different roles to play for the admin


## Philosophy - design implications

The exchange platform is intended for informal exchange among friends

The value of token depends on the acquisition, but it is mostly a natural process, as a private concern of the pool-members. In fact,
the comparison, or monetary-description/representation, has no real meaning, and is only used as a way to purchase tokens.

Pool token size can only grow, tokens can not be taken out. Depending on how the pool is managed, the token-value does not decrease over time, unless it's reflects the pool-members' exchange behaviour.

Do not buy more tokens than you can miss!

A member cannot transfer an amount larger than its balance

A member can transfer at maximum once a minute

There are no guarantees (at first)

The members pay for the platform usage, either by a monthly pool contribution or financed by the token purchase - depends on the pool type

## Run 

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

