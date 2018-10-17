---
title: "Toolkit Usage Overview"
permalink: /docs/user/overview/
excerpt: "How to use this toolkit."
last_modified_at: 2018-02-22T12:37:48+01:00
redirect_from:
   - /theme-setup/
sidebar:
   nav: "userdocs"
---
{% include toc %}
{%include editme %}

# Introduction

The Message Hub toolkit contains two operators, the *MessageHubConsumer*, and the *MessageHubProducer*.
The MessageHubConsumer operator consumes messages from an Event Streams topic and creates Tuples which are processed by
other downstream operators of the Streams application. It is a source operator within your Streams application.

The MessageHubProducer operator creates Kafka messages in the Event Streams service from tuples and acts therefore
as a sink operator within your Streams application.

For both, the MessageHubConsumer and the MessageHubProducer there is a one-to-one relationship between tuples and Kafka messages.
Read more about how to use these operators in the [SPL documentaion](https://ibmstreams.github.io/streamsx.messagehub/doc/spldoc/html/).

# Common consumer patterns and use cases

## Use of consistent region

Kafka itself has the capability of at-least-once delivery from producers to consumers. To keep this delivery semantics within Streams applications consuming messages from Kafka topics, it is recommended to consider using a consistent region within the Streams application unless used operators do not support consistent region.

## Overview

Assumptions:
* One consumer operator consumes messages from one single topic with a string message, for example a JSON message
* For a production environment, the consumer starts consuming at the default start position
* Kafka guarantees no ordering of messages accross partitions.

There are three standard patterns for Streams reading messages from Kafka.

* [**All partitions**](https://ibmstreams.github.io/streamsx.kafka/docs/user/UsecaseAllPartitions/) - A single `MessageHubConsumer` invocation consumes all messages from all partitions of a topic
* [**Kafka consumer group**](https://ibmstreams.github.io/streamsx.kafka/docs/user/UsecaseConsumerGroup/) - the partitions of a topic are automatically assigned to multiple `MessageHubConsumer` invocations for consumption
* [**Assigned partitions**](https://ibmstreams.github.io/streamsx.kafka/docs/user/UsecaseAssignedPartitions/) - Multiple `MessageHubConsumer` invocations with each invocation assigned specific partitions.

 **Note:** The links point to the Kafka toolkit, but the patterns are the same for the MessageHub toolkit. Adapting the pages specific to the MessageHub toolkit is a TODO.

The MessageHubConsumer operator can be configured with additional 
[Kafka consumer properties](https://kafka.apache.org/10/documentation.html#newconsumerconfigs). These can be specified in a property file or in the application configuration that contains also the credentials. The following examples in the standard patterns use a property file in the etc directory of the application's toolkit. Some operator parameters, like **groupId**, and **clientId** map directly to properties. Other properties are adjusted by the operator. Which one, can be reviewed in the [SPL documentation](https://ibmstreams.github.io/streamsx.messagehub/docs/user/SPLDoc/) of the operators.

**Property file example**
```
max.poll.records=2000
# property files can also contain comments and empty lines

# a consumer group identifier can also specified via 'groupId' operator parameter
group.id=myConsumerGroup
```

# Samples

* [MessageHubAppConfigSample](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubAppConfigSample)
* [MessageHubFileSample](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubFileSample)
* [MessageHubConsumerInputPortSample](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubConsumerInputPortSample)
* [MessageHubConsumerGroupWithConsistentRegion](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubConsumerGroupWithConsistentRegion)


It is also worth looking at the samples of the Kafka toolkit, which can be found [here](https://ibmstreams.github.io/streamsx.kafka/docs/user/overview/).
