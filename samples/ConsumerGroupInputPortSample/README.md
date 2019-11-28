# Consumer group with input port

This sample demonstrates how to create a consumer group that subscribes via control input port.
The MessageHubConsumer operator is configured with operator driven checkpointing.

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

In the Event Streams service the topics `t1` and `t2` must be created with at least two partitions each.
To run this sample, create a file called `eventstreams.json` in the `etc/` folder, paste the Event Streams Service Credentials JSON into this file and save it.

Compile the sample with `make` or `gradle` and submit the job with
`streamtool submitjob ./output/com.ibm.streamsx.messagehub.sample.ConsumerGroupInputPortSample/com.ibm.streamsx.messagehub.sample.ConsumerGroupInputPortSample.sab`.
