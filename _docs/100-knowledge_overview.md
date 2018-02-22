---
title: "Toolkit technical background overview"
permalink: /docs/knowledge/overview/
excerpt: "Basic knowledge of the toolkits technical domain."
last_modified_at: 2018-01-10T12:37:48+01:00
redirect_from:
   - /theme-setup/
sidebar:
   nav: "knowledgedocs"
---
{% include toc %}
{% include editme %}

## Purpose of this toolkit

The streamsx.messaging toolkit provides those operators that help you integrate IBM Streams with the 
[IBM Cloud Message Hub service](https://console.bluemix.net/docs/services/MessageHub/index.html#messagehub).
This toolkit lets you read messages from Message Hub topics and write messages into topics.

## Introduction into Apache Kafka

Since the IBM Message Hub service is based on the open-source project Apache Kafka, you should have a certain
degree of knowledge about Apache Kafka. A first place to get introduced with Kafka is the
[Apache Kafka home page](https://kafka.apache.org/intro).

## Introduction into this toolkit

This toolkit is based on the [Kafka toolkit](https://ibmstreams.github.io/streamsx.kafka/) from GitHub, so that its
usage patterns, features, and limitations also apply to this toolkit.

This [Streamsdev article](https://developer.ibm.com/streamsdev/2017/08/10/introducing-messagehub-toolkit/)
gives a good introduction to the Message Hub toolkit.

## Improving consumer performance
[Article on Streamsdev on how to improve performance](https://developer.ibm.com/streamsdev/docs/improving-application-throughput-consuming-kafka/)
when consuming from Kafka.
