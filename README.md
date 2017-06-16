# MessageHub Toolkit

A toolkit that simplifies integration between IBM Streams and the BlueMix MessageHub service. 


## Documentation

Toolkit documentation can be found here: SPLDoc (COMING SOON!)


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

Add the MessageHub JSON credentials to the `tests/KafkaTests/etc/messagehub.json` file.

Run the following commands to launch the tests:

```
cd tests/KafkaTests
../../gradlew test
```

**NOTE:** Tests will run using the local domain specified by the *STREAMS_DOMAIN_ID* env var. All tests run in Distributed mode.


## Samples

Each sample contains a *build.gradle* file. The samples can be built/compiled by running `gradle build` from the sample directory.
