/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            + "file called `eventstreams.json`. When the operator starts up it will read the credentials from that file and "
            + "extract the information needed to connect. The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create a file called `eventstreams.json` in the `<application_directory>/etc/` directory. \\n"
            + " 2. Paste the Event Streams credentials JSON into this file and save it. \\n"
            + " 3. The operator will automatically look for the file `<application_directory>/etc/eventstreams.json` and "
            + "will extract the information needed to connect.\\n"
            + "\\n"
            + "**NOTE:** Users can use the **credentialsFile** parameter to specify a different file containing "
            + "the Event Streams service credentials JSON.\\n"
            ;

    public static final String SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY= ""
            + "**3. Save Credentials in an Application Configuration Property**\\n"
            + "\\n"
            + "With this option, users can copy their service credentials JSON from the Event Streams service and "
            + "store it in an application configuration property called `eventstreams.creds`. When the operator starts, "
            + "it will look for this property and extract the information needed to connect. "
            + "The following steps outline how this can be done: \\n"
            + "\\n"
            + " 1. Create an application configuration called `eventstreams`.\\n"
            + " 2. Create a property in the `eventstreams` application configuration with *name* `eventstreams.creds`. "
            + " The *value* of the property should be the raw service credentials JSON of the Event Streams service instance.\\n"
            + " 3. The operator will automatically look for an application configuration named `eventstreams` and will extract "
            + "the information needed to connect.\\n"
            + "\\n"
            + "**NOTE 1:** Users can specify a different application configuration name by setting the **appConfigName** "
            + "parameter. The operator will still look for a property called `eventstreams.creds` containing the "
            + "Event Streams service credentials in JSON format. \\n"
            + "\\n"
            + "**NOTE 2:** Users can add generic Kafka properties, for example `metadata.max.age.ms`, or `client.dns.lookup`, to the same "
            + "application configuration, which contains the service credentials. To make the operator use these Kafka properties, "
            + "the **appConfigName** parameter must be specified even if the default application configuration name "
            + "`eventstreams` is used. Looking at the other way round, when the default application configuration name "
            + "`eventstreams` is used, but not specified as **appConfigName** parameter value, only the service credentials "
            + "are used from this application configuration.\\n"
            ;

    public static final String DEFAULT_CREDENTIAL_PARAMETERS = "" 
            + "---\\n"
            + "| Parameter | Default Value | Description |\\n"
            + "|===|\\n"
            + "| **appConfigName** | `eventstreams` | Users can choose to place the raw Event Streams credentials JSON in a property "
            + "called `eventstreams.creds` in an application configuration called `eventstreams`. "
            + "The operator will extract the information needed to connect to Event Streams. |\\n"
            + "|---|\\n"
            + "| **credentialsFile** | `etc/eventstreams.json` | Users can paste the raw Event Streams credentials JSON into "
            + "a file pointed to by this parameter. The operator will extract the information needed to connect to Event Streams. "
            + "By default, the operator will look for a file called `etc/eventstreams.json`. |\\n"
            + "---\\n";


    public static final String PARAM_CREDS_FILE = ""
            + "Specifies the name of the file that contains "
            + "the complete Event Streams service credentials in JSON format. If not specified, this parameter will "
            + "attempt to load the credentials from the file `etc/eventstreams.json` or `etc/messagehub.json` for backward"
            + "compatibility. A relative path is always "
            + "interpreted as relative to the *application directory* of the Streams application.\\n"
            + "\\n"
            + "Credentials stored in a file take priority over credentials stored in an appclication configuration."
            + "This parameter deprecates the **messageHubCredentialsFile** parameter.";
    
    public static final String PARAM_CREDENTIALS = ""
            + "Specifies the credentials of the Event Streams cloud service instance in JSON. "
            + "This parameter takes priority over a credentials file and credentials specified as property in an application configuration.";
}
