# Event Streams Credentials Parameter Sample

This sample demonstrates how to use the **credentials** parameter to connect to an IBM Event Streams service instance in the IBM cloud.
The credentials are passed as **credentials** submission time parameter to the application.

## Setup
In order to successfully run this sample, the following steps must be taken: 

- Create a topic `test` in your Event Streams service instance.


## Build

```
make
```

or if you are in a cloned or forked git repository

```
../../gradlew build
```

## Clean


```
make clean
```

or if you are in a cloned or forked git repository

```
../../gradlew clean
```

## Submitting the application

```
streamtool submitjob output/com.ibm.streamsx.messagehub.sample.MessageHubCredentialsParamSample/com.ibm.streamsx.messagehub.sample.MessageHubCredentialsParamSample.sab \
-P credentials='your service credentials JSON'
```

for example,

```
streamtool submitjob output/com.ibm.streamsx.messagehub.sample.MessageHubCredentialsParamSample/com.ibm.streamsx.messagehub.sample.MessageHubCredentialsParamSample.sab \
-P credentials='{
  "instance_id": "12345",
  "mqlight_lookup_url": "https://mqlight-lookup-url.net/Lookup?serviceId=12345",
  "api_key": "ABCDEFGHIJKLMNOP",
  "kafka_admin_url": "https://kafka-admin-url.net:443",
  "kafka_rest_url": "https://kafka-rest-url.net:443",
  "kafka_brokers_sasl": [
    "kafka03.bluemix.net:9093",
    "kafka01.bluemix.net:9093",
    "kafka02.bluemix.net:9093",
    "kafka04.bluemix.net:9093",
    "kafka05.bluemix.net:9093"
  ],
  "user": "ABCDEFG",
  "password": "HIJKLMNOP"
}' -C tracing=info
```

Note, that the complete credentials are enclosed in single quotes.