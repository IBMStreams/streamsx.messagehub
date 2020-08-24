# Message Hub Toolkit

A toolkit that simplifies integration between IBM Streams and the IBM Cloud Event Streams service.


## Documentation

- [Toolkit reference](https://ibmstreams.github.io/streamsx.messagehub/docs/user/SPLDoc/) for latest and older versions. The reference includes details on ways to configure the toolkit to connect to the Event Streams service.
- Standard use cases for Consumers and Partitions can be found on the [overview page of the user documentation](https://ibmstreams.github.io/streamsx.messagehub/docs/user/overview/):

## Build

```
cd com.ibm.streamsx.messagehub
../gradlew build
```

## Build SPLDoc
```
./gradlew spldoc
```

## Release
```
./gradlew release
```

## Test

- Create an Event Streams service instance in the [IBM Cloud](https://cloud.ibm.com)
- In the IBM Event Streams service, create a topic "test".
- Add the Message Hub JSON credentials to the `tests/MessageHubTests/etc/credentials.json` file.
- Run the following commands to launch the tests:
```
cd tests/MessageHubTests
../../gradlew test
```

**NOTE:** Tests will run using the local domain specified by the *STREAMS_DOMAIN_ID* env var. All tests run in Distributed mode.


## Samples

Each sample contains a `build.gradle` file and a makefile. You can compile/build the samples by
a) running `gradle build` from the sample directory if you have installed gradle,
b) running `../../gradlew build` if you are in a cloned or forked git repository, or
c) running `make` in all other cases.
