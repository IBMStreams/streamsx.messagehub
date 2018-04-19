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

The Message Hub toolkit contains two operators, the *MessageHubConsumer*, and the *MessageHubProducer*.
The MessageHubConsumer operator consumes messages from a Message Hub topic and creates Tuples which are processed by
other downstream operators of the Streams application. It is a source operator within your Streams application.

The MessageHubProducer operator creates Kafka messages in the Message Hub service from tuples and acts therefore
as a sink operator within your Streams application.

For both, the MessageHubConsumer and the MessageHubProducer there is a one-to-one relationship between tuples and Kafka messages.
Read more about how to use these operators in the [SPL documentaion](/streamsx.messagehub/doc/spldoc/html/index.html).

### Samples

* [MessageHubAppConfigSample](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubAppConfigSample)
* [MessageHubFileSample](https://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubFileSample)
* [MessageHubConsumerInputPortSample](ttps://github.com/IBMStreams/streamsx.messagehub/tree/develop/samples/MessageHubConsumerInputPortSample)

It is also worth looking at the samples of the Kafka toolkit, which can be found [here](https://ibmstreams.github.io/streamsx.kafka/docs/user/overview/).
