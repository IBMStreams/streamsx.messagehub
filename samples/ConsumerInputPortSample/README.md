# MessageHubConsumer with control port Sample

This sample demonstrates how to use the control port to start and stop consuming from topic partitions at
specific positions.

## Setup
In order to successfully run this sample, the following steps must be taken:

 1. In the application directory, create a folder named `etc` if not present
 1. Within the `etc/` folder, create a file called `eventstreams.json`
 1. Paste the Event Streams Service Credentials JSON into this file and save it
 1. Create the topics `t1` and `t2` with two partitions each in your Event Streams service instance. When the topics already exist, it is recommended to remove and recreate them.
 1. Rebuild the application and launch

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
