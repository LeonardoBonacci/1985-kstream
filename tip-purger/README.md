# Cleans-up the Redis cache

Trick applied to avoid a complicated exactly once semantics implementation with offsets stored in datastore.

We only delete TIP from cache when both the key:poolAccountId and the value:transferId correspond.
This way, in case of at least once > 1 scenario's, a 'new' poolAccountId transfers is not accidently deleted.

The delete of *from* and *to* together happens atomically.