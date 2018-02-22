---
title: "Operator Design"
permalink: /docs/user/OperatorDesign/
excerpt: "Describes the design of the Message Hub toolkit operators."
last_modified_at: 2018-02-22T12:37:48+01:00
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

 * Sets a default value for the **appConfigName** parameter to `messagehub`. Users can specify connectivity properties 
 (i.e. bootstrap.servers, sasl.jaas.config, etc) in an application configuration named `messagehub` and the operator will 
 automatically read those properties without the user needing to specify a value the **appConfigName** parameter. 
 * In addition to setting a default value for the **appConfigName** parameter, the operator looks for a special property 
 named `messagehub.creds` in the application configuration. Users can store the entire Message Hub credentials JSON in this 
 property. The operator parses the JSON and automatically configures all of the necessary properties in order to connect to Message Hub. 
 * A new parameter called **messageHubCredsFile** allows the user to store the Message Hub credentials JSON in a file
 rather than in an application configuration. This parameter is useful for users who want to test and debug their applications 
 while running in standalone mode.

### Parameters

| Parameter Name | Default | Description |
| --- | --- | --- |
| messageHubCredsFile | `etc/messagehub.json` | Specifies the name of the file that contains the entire MessageHub credentials JSON. By default, this parameter will look for a file named `messagehub.json` in the applications `etc` directory. |

