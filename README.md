# Message Hub Toolkit

A toolkit that simplifies integration between IBM Streams and the IBM Cloud Message Hub service. 


## Documentation

The toolkit documentation, including details on ways to configure
the toolkit to connect to Message Hub can be found here: [https://ibmstreams.github.io/streamsx.messagehub/](https://ibmstreams.github.io/streamsx.messagehub/)


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

Create a Message Hub Service in [IBM Cloud](https://console.bluemix.net)

In the IBM Cloud Message Hub service, create a topic "test"

Add the Message Hub JSON credentials to the `tests/MessageHubTests/etc/messagehub.json` file.

Run the following commands to launch the tests:

```
cd tests/MessageHubTestsTests
../../gradlew test
```

**NOTE:** Tests will run using the local domain specified by the *STREAMS_DOMAIN_ID* env var. All tests run in Distributed mode.


## Samples

Each sample contains a *build.gradle* file. The samples can be built/compiled by running `gradle build` from the sample directory.
