# MessageHub Toolkit

A toolkit that simplifies integration between IBM Streams and the BlueMix MessageHub service. 


## Documentation

The toolkit documentation, including details on ways to configure
the toolkit to connect to MessageHub can be found here: [https://ibmstreams.github.io/streamsx.messagehub/](https://ibmstreams.github.io/streamsx.messagehub/)


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

Create a MessageHub Service in [IBM Bluemix](https://console.bluemix.net)

In the IBM Bluemix MessageHub service, create a topic "test"

Add the MessageHub JSON credentials to the `tests/MessageHubTests/etc/messagehub.json` file.

Run the following commands to launch the tests:

```
cd tests/MessageHubTestsTests
../../gradlew test
```

**NOTE:** Tests will run using the local domain specified by the *STREAMS_DOMAIN_ID* env var. All tests run in Distributed mode.


## Samples

Each sample contains a *build.gradle* file. The samples can be built/compiled by running `gradle build` from the sample directory.
