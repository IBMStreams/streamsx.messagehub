# Event Streams Consumer Group with cooperative incremental rebalancing

This sample demonstrates how to enable the [cooperative incremental rebalance protocol](https://www.confluent.io/blog/cooperative-rebalancing-in-kafka-streams-consumer-ksqldb/)
for large consumer groups, for examle when you have an [enterprise plan](https://cloud.ibm.com/docs/EventStreams?topic=EventStreams-plan_choose)
that allows for a large number of topic partitions. Large consumer groups and large number of topic partitions are required for ingest of
high volume data from Event Streams.

Please note, that cooperative incremental rebalancing is incompatible with consistent region as the consistent region features are of "stop-the-world" type.
When you modify this sample, and enabling consistent region, automatically the legacy eager rebalance protocol is used.

### Setup

In the Event Streams service, a *partitioned* topic with name `testtopic` and for example, 20 partitions must be created. If a different topic name is used, the name
must be configured as `$topic` composite parameter in the main composite.

To run this sample, add your JSON credentials of the Event Streams service to `etc/eventstreams.json`.

Compile the sample with `make` or `gradle` and submit the job with
`streamtool submitjob --config parallelRegionConfig=channelIsolation ./output/com.ibm.streamsx.messagehub.sample.ConsumerGroupCooperativeIncremental/com.ibm.streamsx.messagehub.sample.ConsumerGroupCooperativeIncremental.sab`
to your local Streams installation, or submit the application bundle to the Streaming Analytics Service or CloudPak for Data.

Don't forget to rebuild the application when you change Kafka properties in one of the property files for the consumer or producer because they go
into the application's bundle file.
