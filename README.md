# Heroes - Token exchange service for local communities


```
curl localhost:8080/accounts/a/heroes

curl localhost:8080/accounts/balance/a/heroes

curl -d '{"from":"b", "to":"a", "amount":10.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers/heroes
curl -d '{"from":"b", "to":"a", "amount":100.10}' -H "Content-Type: application/json" -X POST localhost:8080/transfers/heroes

```

## TODO
fix the input validation

fix the unit-tests

create mechanism to read pool-members from yml


## Basic concepts

gatekeeper creates an empty pool

there are different types of pools
1 demo -> currency fixed
2 gatekeeper controlled
3 currency linked

TODO something about tokens?

the gatekeeper can invite members to the pool

a pool-member can purchase tokens within the pool

each pool-member has a balance, represented in pool-tokens
pool-members can transfer tokens to each other - given that they have enough balance for the transfer

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

the gatekeeper does not profit financially on the platform for managing pools 

### The platform

the platform offers people/members a shielded realm to exchange tokens with other members in the form of pools

different types of pools maintain different member-entrance and token-acquisition policies - with different roles to play for the gatekeeper


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



