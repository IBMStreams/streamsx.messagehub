# Message Hub File Sample

This sample demonstrates how to create an application that connects to Message Hub by retrieving the Message Hub Service Credentials from a file packaged with the application. 

## Setup
In order to successfully run this sample, the following steps must be taken:

 1. In the application directory, create a folder named `etc`
 2. Within the `etc/` folder, create a file called `messagehub.json`
 3. Paste the Message Hub Service Credentials JSON into this file and save it
 4. Rebuild the application and launch

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
