# Message Hub Application Configuration Sample

This sample demonstrates how to create an application that connects to Message Hub by retrieving the Message Hub Service Credentials from an application configuration. 

## Setup
In order to successfully run this sample, the following steps must be taken: 

 1. Open the Streams Console
 2. On the navigation bar at the top, open the **Application Dashboard**
 3. On the left side, click the **Application Configuration** button
 4. Click on the **Application Configuration** tab
 5. Click the **Add Configuration** button
 6. In the **name** field, set the value to `messagehub`
 7. Add a property to the table with the following details: 
    * **Name:** `messagehub.creds`
    * **Value:** &lt;Message Hub_Service_Credentials_JSON&gt;
 8. Save the application configuration


## Build

```
../../gradlew build
```

## Clean

```
../../gradlew clean
```
