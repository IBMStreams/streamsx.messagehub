---
title: "Operator Design"
permalink: /docs/user/OperatorDesign/
excerpt: "Describes the design of the Message Hub toolkit operators."
last_modified_at: 2019-10-02T12:37:48+01:00
redirect_from:
   - /theme-setup/
sidebar:
   nav: "userdocs"
---
{% include toc %}
{%include editme %}

This Message Hub toolkit contains two operators to enable faster connectivity to the IBM Cloud Message Hub service. These operators include:

 * **MessageHubConsumer** - this operator is a wrapper around the `KafkaConsumer` operator ([design documentation](https://ibmstreams.github.io/streamsx.kafka/docs/user/KafkaConsumerDesign)).
 * **MessageHubProducer** - this operator is a wrapper around the `KafkaProducer` operator ([design documentation](https://ibmstreams.github.io/streamsx.kafka/docs/user/KafkaProducerDesign)).

The goal of these operators is to make connectivity to the Message Hub service as simple as possible and with minimal setup from the user.

To accomplish this goal, the operators provides the following functionality:

 * Sets a default value for the **appConfigName** parameter to `eventstreams`.
 * In addition to setting a default value for the **appConfigName** parameter, the operator looks for a special property
   named `eventstreams.creds` in the application configuration. Users can store the entire Message Hub credentials JSON in this
   property. The operator parses the JSON and automatically configures all of the necessary properties in order to connect to
   the Event Streams service.
 * A parameter called **credentialsFile** allows the user to store the Event Streams credentials JSON in a file
   rather than in an application configuration. This parameter is useful for users who want to test and debug their applications.
 * The parameter **credentials** can be used to specify the credentials JSON string, for example as a submission time parameter.

The priority of the above options is

1. **credentials** operator parameter (both file and application config are ignored)
1. credentials stored in a file, also when the default filename `etc/eventstreams.json` is used (application config is ignored)
1. application configuration

Users can specify connectivity properties (i.e. `reconnect.backoff.max.ms`, `reconnect.backoff.ms`, etc) in an application configuration
like for the *streamsx.kafka* toolkit. When doing so, the application configuration name must be specified by using the
**appConfigName** parameter - even when the application configuration has the default name `eventstreams`.

### Parameters

| Parameter Name | Default | Description |
| --- | --- | --- |
| credentials | - | Specifies the credentials of the Event Streams cloud service instance in JSON. |
| credentialsFile | `etc/eventstreams.json` | Specifies the name of the file that contains the entire Event Streams credentials JSON. By default, this parameter will look for a file named `eventstreams.json` in the applications `etc` directory. |
| appConfigName | `eventstreams` | Specifies the name of the application configuration that contains the entire Event Streams credentials JSON. |

