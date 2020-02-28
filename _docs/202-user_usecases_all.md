---
title: "Usecase: Consume All Partitions"
permalink: /docs/user/UsecaseAllPartitions/
excerpt: "How to use this toolkit."
last_modified_at: 2019-12-03T12:37:48+01:00
redirect_from:
   - /theme-setup/
sidebar:
   nav: "userdocs"
---
{% include toc %}
{%include editme %}

# Overview

A single `MessageHubConsumer` operator consumes all messages from a topic regardless of the number of partitions.

# Details

Without a partition specification, the operator will consume from all partitions of the topic. The partitions of the subscribed topic are assigned by Kafka, and the operator represents a consumer group with only one member. When no group identifier is specified, the consumer operator will create an identifier. With group management enabled, the operator will automatically be assigned new partitions, when partitions are added to the topic. On the other side, partition de-assignment and re-assignment can happen when

* Group management related timers expire
* The broker node being the group coordinator goes down
* Meta data of the subscribed topic changes, for example the number of partitions

Partition re-assignment makes the consumer replay Kafka messages beginning with last committed offsets.

**Note:** MessageHub toolkits with version **below 3.0** behave different when *no group identifier* is specified:

The operator self-assignes to all partitions of the topic. When new partitions are added to the topic, the PE that contains the operator must be restarted to read also added partitions.

# Pros and Contras

* **Pro:** Very simple
* **Con:** Volume is limited by a single operator reading messages from all partitions

# Guaranteed processing

* Consistent region: Supported (periodic and operator driven)
* Checkpointing via `config checkpoint`: Supported, but ignored. The operator does not save any data.

When the operator is used in a consistent region, at least once processing through the Streams application is guaranteed.
Without a consistent region, tuples can get lost within the Streams application when a PE restarts.

# Operator configuration

No assignment of partitions is configured through the **partition** operator parameter (**partition** parameter not used).

# Examples
## Consume messages without a key
```
composite ConsumeAllPartitions {
graph
    stream <rstring json> Messages = MessageHubConsumer() {
        param
            // always specify the app config name when the app config
            // contains Kafka properties besides the credentials
            appConfigName: "ConsumeEventStreams";
            topic: "myTopic";
            outputMessageAttributeName: "json";
    }

    ...
}
```

## Consume keyed messages within an operator driven consistent region
```
composite ConsumeAllPartitionsCrOperatorDriven {
graph
    () as JCP = JobControlPlane() {}

    @consistent (trigger = operatorDriven)
    stream <rstring json, rstring messageKey> Messages = MessageHubConsumer() {
        param
            appConfigName: "ConsumeEventStreams";
            topic: "myTopic";
            outputMessageAttributeName: "json";
            outputKeyAttributeName: "messageKey";
            triggerCount: 10000;   // make the region consistent every 10000 tuples
    }

    ...
}
```

## Consume keyed messages within a periodic consistent region
```
composite ConsumeAllPartitionsCrPeriodic {
graph
    () as JCP = JobControlPlane() {}

    @consistent (trigger = periodic, period = 60.0)
    stream <MessageType.StringMessage> Messages = MessageHubConsumer() {
        // MessageType.StringMessage is tuple<rstring message, rstring key>
        param
            appConfigName: "ConsumeEventStreams";
            topic: "myTopic";
    }

    ...
}
```

`message` and `key` are the default attribute names for the Kafka message and the key. They need not be specified as output attribute names.