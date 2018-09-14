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
            + "| ssl.truststore | JKS |\\n"
            + "|---|\\n"
            + "| ssl.enabled.protocols | TLSv1.2 |\\n"
            + "|---|\\n"
            + "| ssl.endpoint.identification.algorithm | HTTPS |\\n"
            + "---\\n";

    public static final String SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY= ""
            + "**Save Credentials in Application Configuration Property**\\n"
            + "\\n"
            + "With this option, users can copy their Message Hub Credentials JSON from the Message Hub service and "
            + "store it in an application configuration property called `messagehub.creds`. When the operator starts, "
            + "it will look for that property and extract the information needed to connect. "
            + "The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create an application configuration called `messagehub`.\\n"
            + " 2. Create a property in the `messagehub` application configuration with *name* `messagehub.creds`.\\n"
            + "   * The *value* of the property should be the raw Message Hub Credentials JSON\\n"
            + " 3. The operator will automatically look for an application configuration named `messagehub` and will extract "
            + "the information needed to connect. Users only need to specify the topic(s) that they wish to consume messages "
            + "from (set via the **topic** parameter).\\n"
            + "\\n"
            + "**NOTE 1:** Users can specify a different application configuration name by setting the **appConfigName** "
            + "parameter. The operator will still look for a property called `messagehub.creds` containing the "
            + "Message Hub Credentials JSON. \\n"
            + "\\n"
            + "**NOTE 2:** Users can add generic Kafka properties, for example client.id, to the application configuration "
            + "that already contains the service credentials. To make the operator use these Kafka properties, "
            + "the **appConfigName** parameter must be specified even if the default application configuration name "
            + "`messagehub` is used. Looking at the other way round, when the default application configuration name "
            + "`messagehub` is used, but not specified as **appConfigName** parameter value, only the service credentials "
            + "are used from the application config.\\n";

    public static final String SAVE_CREDENTIALS_IN_FILE = ""
            + "**Save Credentials in File**\\n"
            + "\\n"
            + "With this option, users can copy their Message Hub credentials JSON from the Message Hub service and store it in a "
            + "file called `messagehub.json`. When the operator starts up it will read the credentials from that file and "
            + "extract the information needed to connect. The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create a file called `messagehub.json` in the `<application_directory>/etc/` directory. \\n"
            + " 2. Paste the Message Hub Credentials JSON into this file and save it. \\n"
            + " 3. The operator will automatically look for the file `<application_directory>/etc/messagehub.json` and "
            + "will extract the information needed to connect.\\n"
            + "\\n"
            + "**NOTE:** Users can use the **messageHubCredentialsFile** parameter to specify a different file containing "
            + "the Message Hub Credentials JSON.\\n";

    public static final String DEFAULT_CREDENTIAL_PARAMETERS = "" 
            + "---\\n"
            + "| Parameter | Default Value | Description |\\n"
            + "|===|\\n"
            + "| **appConfigName** | `messagehub` | Users can choose to place the raw MesageHub Credentials JSON in a property called `messagehub.creds` in an application configuration called `messagehub`. The operator will extract the information needed to connect to Message Hub. |\\n"
            + "|---|\\n"
            + "| **messageHubCredentialsFile** | `etc/messagehub.json` | Users can paste the raw Message Hub Credentials JSON into a file pointed to by this parameter. The operator will extract the information needed to connect to Message Hub. By default, the operator will look for a file called `etc/messagehub.json`. |\\n"
            + "---\\n";
}
