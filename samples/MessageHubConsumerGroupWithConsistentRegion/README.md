# MessageHubConsumerGroupWithConsistentRegion

This sample demonstrates how to use the MessageHubConsumer operator in a Kafka consumer group within a Consistent Region.
In this configuration, the Streams application benefits from both Kafka consumer groups and the consistent region
features for at least once processing.
In a consumer group, the partitions of the subscribed topics are dynamically assigned to the available Consumers by Kafka.

When in a consumer group with multiple consumers, the *group of consumers* replays the Kafka messages on reset of
the consistent region. After reset, a consumer operator may replay tuples that a different consumer operator has generated 
before the reset happened. The application design must reflect this property of the consumer group.

To make this sample work, these preconditions must be met:
* The Streams instance or domain must be configured with a checkpoint repository.


### Setup

Make sure that either the properties
```
instance.checkpointRepository
instance.checkpointRepositoryConfiguration
```
or
```
domain.checkpointRepository
domain.checkpointRepositoryConfiguration
```
have valid values. For example, if you use a local redis server, you can set the properties to following values:
```
instance.checkpointRepository=redis
instance.checkpointRepositoryConfiguration={ "replicas" : 1, "shards" : 1, "replicaGroups" : [ { "servers" : ["localhost:6379"], "description" : "localhost" } ] }
```
Use the command `streamtool getproperty -a | grep checkpoint` and `streamtool getdomainproperty -a | grep checkpoint` to see the current values.

In the Kafka broker, a *partitioned* topic with name `testtopic` and four partitions must be created. If a different topic name is used, the name 
must be configured as `$topic` composite parameter in the main composite. 
The Kafka message producer in this example distributes the messages round-robin to the partitions. The number of partitions must be configured for
the producer `nPartitions` composite parameter.
To run this sample, add your JSON credentials of the MessageHub service to `etc/messagehub.json`.

Compile the sample with `make` or `gradle` and submit the job with 
`streamtool submitjob --config parallelRegionConfig=channelIsolation ./output/com.ibm.streamsx.messagehub.sample.MessageHubConsumerGroupWithConsistentRegion/com.ibm.streamsx.messagehub.sample.MessageHubConsumerGroupWithConsistentRegion.sab` 
to your local Streams installation, or submit the application to the Streaming Analytics Service.

Don't forget to rebuild the application when you change Kafka properties in one of the property files for the consumer or producer because they go 
into the application's bundle file.
