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

import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator;
import com.ibm.streamsx.kafka.operators.KafkaSplDoc;
import com.ibm.streamsx.messagehub.operators.utils.ServiceCredentialsUtil;

@PrimitiveOperator(name = "MessageHubProducer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubProducerOperator.DESC)
@Icons(location16 = "icons/MessageHubProducer_16.png", location32 = "icons/MessageHubProducer_32.png")
@Libraries({"opt/downloaded/*", "impl/lib/*"})
@InputPorts({ @InputPortSet(description = "Port that consumes tuples. Each tuple is written as a record to the configured topic(s).", cardinality = 1, optional = false) })
@OutputPorts({
    @OutputPortSet(description = "This port is an optional output port. Dependent on the "
            + "**outputErrorsOnly** parameter, "
            + "the output stream includes only tuples for input tuples that failed to get published on one or all "
            + "of the specified topics, or it contains tuples corresponding to *all* input tuples, successfully produced "
            + "ones and failed tuples.\\n"
            + "\\n"
            + "The output port is asynchronous to the input port of the operator. The sequence of the submitted tuples "
            + "may also differ from the sequence of the input tuples. Window punctuations from the input stream are not forwarded.\\n"
            + "\\n"
            + "The schema of the output port must consist of one optional attribute of tuple type with the same schema "
            + "as the input port and one optional attribute of type `rstring`, `ustring`, `optional<rstring>`, or `optional<ustring>`, "
            + "that takes a JSON formatted description "
            + "of the occured error, or remains *empty* for successfully produced tuples. Emptiness of the attribute means that the attribute contains a "
            + "string with zero length when declared as `rstring` or `ustring`, and an empty optional (optional without a value) when declared as optional. "
            + "Both attributes can have any names and can be declared in any sequence in the schema.\\n"
            + "\\n"
            + "**Example for declaring the output stream as error output:**\\n"
            + "\\n"
            + "    stream <Inp failedTuple, rstring failure> Errors = MessageHubProducer (Data as Inp) {\\n"
            + "        ...\\n"
            + "    }\\n"
            + ""
            + "**Example of the failure description**, which would go into the *failure* attribute above:\\n"
            + "\\n"
            + "    {\\n"
            + "        \\\"failedTopics\\\":[\\\"topic1\\\"],\\n"
            + "        \\\"lastExceptionType\\\":\\\"org.apache.kafka.common.errors.TopicAuthorizationException\\\",\\n"
            + "        \\\"lastFailure\\\":\\\"Not authorized to access topics: [topic1]\\\"\\n"
            + "    }\\n"
            + ""
            + "Please note that the generated JSON does not contain line breaks as in the example above, where the JSON has "
            + "been broken into multiple lines to better show its structure.\\n"
            + "\\n"
            + "**Example for declaring the output stream for both successfully produced input tuples and failures:**\\n"
            + "\\n"
            + "    stream <Inp inTuple, optional<rstring> failure> ProduceStatus = MessageHubProducer (Data as Inp) {\\n"
            + "        param\\n"
            + "            outputErrorsOnly: false;\\n"
            + "        ...\\n"
            + "    }\\n"
            + "",
            cardinality = 1, optional = true, windowPunctuationOutputMode = WindowPunctuationOutputMode.Free)})
public class MessageHubProducerOperator extends AbstractKafkaProducerOperator {

    private static final Logger logger = Logger.getLogger(MessageHubProducerOperator.class);

    private String credentialsFilename = null;
    private String credentials = null;
    private ServiceCredentialsUtil credentialsUtil = null;

    @Parameter (optional = true, name = "credentials", description = SplDoc.PARAM_CREDENTIALS)
    public void setCredentials (String credentials) {
        this.credentials = credentials;
    }

    @Parameter(optional = true, name = "credentialsFile", description = SplDoc.PARAM_CREDS_FILE)
    public void setCredsFile (String filename) {
        this.credentialsFilename = filename;
    }


    @Override
    protected void loadProperties() throws Exception {
        if (this.credentialsUtil == null) {
            this.credentialsUtil = new ServiceCredentialsUtil (this, this.credentials, this.appConfigName, this.credentialsFilename, getKafkaProperties());
        }
        credentialsUtil.loadPropertiesFromParameterOrFile();
        // collect Kafka properties
        // super.loadProperties reads 1. from properties file, 2. from app config (where the overwritten loadFromAppConfig() is invoked)
        super.loadProperties();
    }


    @Override
    protected void loadFromAppConfig() throws Exception {
        if (this.credentialsUtil == null) {
            this.credentialsUtil = new ServiceCredentialsUtil (this, this.credentials, this.appConfigName, this.credentialsFilename, getKafkaProperties());
        }
        Map <String, String> appCfgAndPropName = credentialsUtil.loadFromAppConfigCredentials();
        
        // load more (Kafka) properties from the app config, but remove the key where we have found the credentials
        super.loadFromAppConfig();
        for (String propName: appCfgAndPropName.values()) {
            logger.info ("removing " + propName + " from Kafka properties");
            getKafkaProperties().remove (propName);
        }
    }


    public static final String DESC = ""
            + "The **MessageHubProducer** operator is used to consume records from the IBM Event Streams cloud service. "
            + "The operator has been designed to make connectivity to the service as simple as possible. "
            + "This is achieved in a number of different ways, from having default values for the **appConfigName** parameter "
            + "to allowing the user to copy/paste the raw service credentials JSON into either an application configuration "
            + "property or a file.\\n"
            + "\\n"
            + "The following table lists the default values that have been set by this operator for a couple of parameters: \\n"
            + "\\n"
            + SplDoc.DEFAULT_CREDENTIAL_PARAMETERS
            + "\\n"
            + "\\n"
            + "# Setup\\n"
            + "\\n"
            + "This section outlines different options for enabling the **MessageHubProducer** operator to connect to "
            + "the IBM Event Streams cloud service. "
            + "Any of the following options can be used to configure the operator for connecting to IBM Cloud. \\n"
            + "\\n"
            + "* save the credentials in an application configuration - it is the best way to protect your sensitive data\\n"
            + "* save the credentials in a file, which is bundled with the Streams application bundle\\n"
            + "* specify the credentials as **credentials** operator parameter. Any SPL rstring expression can be specified as operator parameter, for example `getSubmissionTimeValue(\\\"credentials\\\");`.\\n"
            + "\\n"
            + "The priority of the above options is\\n"
            + "1. **credentials** operator parameter (both file and application config are ignored)\\n"
            + "1. credentials stored in a file, also when the default filename `etc/eventstreams.json` is used (application config is ignored)\\n"
            + "1. application configuration\\n"
            + "\\n"
            + SplDoc.CREDENTIALS_PARAM
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_FILE
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY
            + "\\n"
            + "\\n"
            + "Users only need to specify the topic that they wish to produce messages to (set via the **topic** parameter).\\n"
            + "\\n"
            + "# Additional Operator Details\\n"
            + "\\n"
            + "# Kafka Properties\\n"
            + "\\n"
            + "The operator implements Kafka's Producer API. As a result, it supports all "
            + "Kafka properties that are supported by the underlying API. The producer properties "
            + "can be found in the [https://kafka.apache.org/documentation/#producerconfigs|Apache Kafka documentation]. "
            + "Properties can be "
            + "specified in a file or in an application configuration. If specifying properties "
            + "via a file, the **propertiesFile** parameter can be used. If specifying properties "
            + "in an application configuration, the name of the application configuration must be "
            + "specified using the **appConfigName** parameter. \\n"
            + "\\n"
            + "# Kafka consumer properties that cannot be overwritten by the user\\n"
            + "\\n"
            + SplDoc.KAFKA_PROPERTIES_DERIVED_FROM_CREDENTIALS
            + "\\n"
            + "These properties cannot be overwritten by specific Kafka properties provided via properties file or "
            + "application configuration. They are ignored when specified in a property file or application configuration. "
            + "In addition to the properties above, the following properties are set by default or adjusted:\\n"
            + "\\n"
            + "# Kafka producer properties that are setup or adjusted by the operator"
            + "\\n"
            + KafkaSplDoc.PRODUCER_DEFAULT_AND_ADJUSTED_PROPERTIES
            + "\\n"
            + "\\n"
            + "# Kafka Properties via Application Configuration\\n"
            + "\\n"
            + "Users can specify Kafka properties using Streams' application configurations. Information "
            + "on configuring application configurations can be found here: "
            + "[https://www.ibm.com/support/knowledgecenter/SSCRJU_4.3.0/com.ibm.streams.admin.doc/doc/"
            + "creating-secure-app-configs.html|Creating application configuration objects to securely "
            + "store data]. Each property set in the application configuration "
            + "will be loaded as a Kafka property. For example, to specify the cipher suites for SSL that "
            + "should be used for secure connections, an app config property named `ssl.cipher.suites` should "
            + "be created.\\n"
            + "\\n"
            + KafkaSplDoc.PRODUCER_AUTOMATIC_SERIALIZATION
            + "\\n"
            + KafkaSplDoc.PRODUCER_EXPOSED_KAFKA_METRICS
            + "\\n"
            + KafkaSplDoc.PRODUCER_CHECKPOINTING_CONFIG
            + "\\n"
            + KafkaSplDoc.PRODUCER_CONSISTENT_REGION_SUPPORT
            + "\\n"
            + "**Transactional message delivery is now supported by all plans of the Event Streams cloud service.**"
            + "\\n"
            + "\\n"
            + "# Error Handling\\n"
            + "\\n"
            + "Many exceptions thrown by the underlying Kafka API are considered fatal. In the event "
            + "that Kafka throws a **retriable exception**, the operator behaves different when used in consistent region or not. "
            + "When used in a consistent region, the operator initiates a reset of the consistent region. The reset processing will instantiate a new "
            + "Kafka producer within the operator.\\n"
            + "\\n"
            + "When the operator is not used within a consistent region, the operator tries to recover internally "
            + "by instantiating a new Kafka producer within the operator and resending all producer records, which are not yet acknowledged. "
            + "Records that fail two producer generations are considered being finally failed. The corresponding tuple is counted in the "
            + "custom metric `nFailedTuples`, and, if the operator is configured with an output port, an output tuple is submitted.\\n"
            + "\\n"
            + "In the event that Kafka throws a **non-retriable exception**, the tuple that caused the exception is counted in the "
            + "custom metric `nFailedTuples`, and, if the operator is configured with an output port, an output tuple is submitted.\\n"
            + "\\n"
            + "Some exceptions can be "
            + "retried by Kafka itself, such as those that occur due to network error. Therefore, it is not recommended "
            + "to set the `retries` property to 0 to disable the producer's "
            + "retry mechanism.";
}
