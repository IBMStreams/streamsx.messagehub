/**
 * 
 */
package com.ibm.streamsx.messagehub.operators;

/**
 * This class contains String constants containing snippets for SPL doc
 */
public class SplDoc {

    public static final String KAFKA_PROPERTIES_DERIVED_FROM_CREDENTIALS = ""
            + "---\\n"
            + "| property name | value |\\n"
            + "|===|\\n"
            + "| bootstrap.servers | *parsed from the service credentials* |\\n"
            + "|---|\\n"
            + "| sasl.jaas.config | org.apache.kafka.common.security.plain.PlainLoginModule required serviceName=\\\"kafka\\\" username=\\\"*username*\\\" password=\\\"*password*\\\"; |\\n"
            + "|---|\\n"
            + "| security.protocol | SASL_SSL |\\n"
            + "|---|\\n"
            + "| sasl.mechanism | PLAIN |\\n"
            + "|---|\\n"
            + "| ssl.protocol | TLSv1.2 |\\n"
            + "|---|\\n"
            + "| ssl.truststore.type | JKS |\\n"
            + "|---|\\n"
            + "| ssl.enabled.protocols | TLSv1.2 |\\n"
            + "|---|\\n"
            + "| ssl.endpoint.identification.algorithm | HTTPS |\\n"
            + "---\\n";

    public static final String CREDENTIALS_PARAM = ""
            + "**1. Use the `credentials` operator parameter**\\n"
            + "\\n"
            + "This option allows you to use any SPL expression that returns an rstring to specify the service credentials. "
            + "As an example, you can write and use an SPL function that retrieves the credentials JSON from a key-value-store.\\n"
            + "\\n"
            + "**Note:** When the **credentials** parameter is specified, credentials which are stored in a file or "
            + "application configuration are ignored. You can specify additional Kafka configs in a property file or "
            + "application configuration, but then you must specify the name of the property file or application configuration "
            + "with the **propertiesFile** or **appConfigName** parameter.\\n";

    public static final String SAVE_CREDENTIALS_IN_FILE = ""
            + "**2. Save Credentials in a File**\\n"
            + "\\n"
            + "With this option, users can copy their credentials JSON from the Event Streams service and store it in a "
            + "file called `messagehub.json`. When the operator starts up it will read the credentials from that file and "
            + "extract the information needed to connect. The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create a file called `messagehub.json` in the `<application_directory>/etc/` directory. \\n"
            + " 2. Paste the Event Streams credentials JSON into this file and save it. \\n"
            + " 3. The operator will automatically look for the file `<application_directory>/etc/messagehub.json` and "
            + "will extract the information needed to connect.\\n"
            + "\\n"
            + "**NOTE:** Users can use the **messageHubCredentialsFile** parameter to specify a different file containing "
            + "the Event Streams service credentials JSON.\\n";
    
    public static final String SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY= ""
            + "**3. Save Credentials in an Application Configuration Property**\\n"
            + "\\n"
            + "With this option, users can copy their service credentials JSON from the Event Streams service and "
            + "store it in an application configuration property called `messagehub.creds`. When the operator starts, "
            + "it will look for that property and extract the information needed to connect. "
            + "The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create an application configuration called `messagehub`.\\n"
            + " 2. Create a property in the `messagehub` application configuration with *name* `messagehub.creds`.\\n"
            + "   * The *value* of the property should be the raw service credentials JSON of the Event Streams service instance.\\n"
            + " 3. The operator will automatically look for an application configuration named `messagehub` and will extract "
            + "the information needed to connect. Users only need to specify the topic(s) that they wish to consume messages "
            + "from (set via the **topic** parameter).\\n"
            + "\\n"
            + "**NOTE 1:** Users can specify a different application configuration name by setting the **appConfigName** "
            + "parameter. The operator will still look for a property called `messagehub.creds` containing the "
            + "Event Streams service credentials in JSON format. \\n"
            + "\\n"
            + "**NOTE 2:** Users can add generic Kafka properties, for example client.id, to the application configuration "
            + "that already contains the service credentials. To make the operator use these Kafka properties, "
            + "the **appConfigName** parameter must be specified even if the default application configuration name "
            + "`messagehub` is used. Looking at the other way round, when the default application configuration name "
            + "`messagehub` is used, but not specified as **appConfigName** parameter value, only the service credentials "
            + "are used from the application config.\\n";

    public static final String DEFAULT_CREDENTIAL_PARAMETERS = "" 
            + "---\\n"
            + "| Parameter | Default Value | Description |\\n"
            + "|===|\\n"
            + "| **appConfigName** | `messagehub` | Users can choose to place the raw Event Streams credentials JSON in a property "
            + "called `messagehub.creds` in an application configuration called `messagehub`. "
            + "The operator will extract the information needed to connect to Event Streams. |\\n"
            + "|---|\\n"
            + "| **messageHubCredentialsFile** | `etc/messagehub.json` | Users can paste the raw Event Streams credentials JSON into "
            + "a file pointed to by this parameter. The operator will extract the information needed to connect to Event Streams. "
            + "By default, the operator will look for a file called `etc/messagehub.json`. |\\n"
            + "---\\n";
}
