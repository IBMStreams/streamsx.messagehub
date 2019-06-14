# Event Streams Application Configuration Sample

This sample demonstrates how to create an application that connects to Event Streamns by retrieving the service credentials from an application configuration. 

## Setup
In order to successfully run this sample, the following steps must be taken: 

 1. Open the Streams Console
 2. On the navigation bar at the top, open the **Application Dashboard**
 3. On the left side, click the **Application Configuration** button
 4. Click on the **Application Configuration** tab
 5. Click the **Add Configuration** button
 6. In the **name** field, set the value to `eventstreams`
 7. Add a property to the table with the following details: 
    * **Name:** `eventstreams.creds`
    * **Value:** &lt;Event Streams Service Credentials JSON&gt;
 8. Save the application configuration


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
