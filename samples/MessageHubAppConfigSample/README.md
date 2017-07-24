# MessageHub File Sample

This sample demonstrates how to connect to MessageHub by storing the credentials in an application configuration. The following steps should be followed to allow the operators to retrieve the MessageHub credentials from an application configuration: 

  * Create an application configuration named `messagehub`
  * Within the `messagehub` app config, create a property called `messagehub.creds` and set the value to the MessageHub credentials JSON
  
The operators will automatically retrieve the credentials from this app config and property in order to connect to MessageHub.


