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
import java.util.Set;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaConsumerOperator;
import com.ibm.streamsx.kafka.operators.KafkaSplDoc;
import com.ibm.streamsx.messagehub.operators.utils.ServiceCredentialsUtil;

@PrimitiveOperator(name = "MessageHubConsumer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubConsumerOperator.DESC)
@Icons(location16 = "icons/MessageHubConsumer_16.png", location32 = "icons/MessageHubConsumer_32.png")
@Libraries({"opt/downloaded/*", "impl/lib/*"})
@InputPorts({
    @InputPortSet(description = ""
            + "This port is used to specify the topics or topic partitions that the consumer should begin reading messages from. When this "
            + "port is specified, the `topic`, `partition` and `startPosition` parameters cannot be used. The operator will only begin "
            + "consuming messages once a tuple is received on this port, which specifies a partition assignment or a topic subscription.\\n"
            + "\\n"
            + "When the MessageHubConsumer participates in a consistent region, only partition assignment via control port is supported. **The support "
            + "of consistent region with the control port is deprecated and may be removed in next major toolkit version.**\\n"
            + "\\n"
            + "When a *topic subscription* is specified, the operator benefits from Kafka's group management (Kafka assigns the partitions to consume). "
            + "When an *assignment* is specified in a control tuple, the operator self-assigns to the given partition(s) of the given topic(s). "
            + "Assignments and subscriptions via control port cannot be mixed. Note, that it is not possible to use both assignment and subscription, "
            + "it is also not possible to subscribe after a previous assignment and unassignment, and vice versa.\\n"
            + "This input port must contain a single `rstring` attribute that takes a JSON formatted string.\\n"
            + "\\n"
            + "**Adding or removing a topic subscription**\\n"
            + "\\n"
            + "To add or remove a topic subscription, the single `rstring` attribute must contain "
            + "a JSON string in the following format:\\n"
            + "\\n"
            + "    {\\n"
            + "      \\\"action\\\" : \\\"ADD\\\" or \\\"REMOVE\\\",\\n" 
            + "      \\\"topics\\\" : [\\n" 
            + "        {\\n"
            + "          \\\"topic\\\" : \\\"topic-name\\\"\\n"
            + "        },\\n" 
            + "        ...\\n" 
            + "      ]\\n"  
            + "    }\\n"
            + "\\n"
            + "The following types and convenience functions are available to aid in creating the JSON string: \\n"
            + "\\n"
            + "* `rstring createMessageAddTopic (rstring topic);`\\n"
            + "* `rstring createMessageAddTopics (list<rstring> topics);`\\n"
            + "* `rstring createMessageRemoveTopic (rstring topic)`\\n"
            + "* `rstring createMessageRemoveTopics (list<rstring> topics);`\\n"
            + "\\n"
            + "\\n"
            + "**Adding or removing a manual partition assignment**\\n"
            + "\\n"
            + "To add or remove a topic partition assignment, the single `rstring` attribute must contain "
            + "a JSON string in the following format:\\n"
            + "\\n"
            + "    {\\n"
            + "      \\\"action\\\" : \\\"ADD\\\" or \\\"REMOVE\\\",\\n" 
            + "      \\\"topicPartitionOffsets\\\" : [\\n" 
            + "        {\\n"
            + "          \\\"topic\\\" : \\\"topic-name\\\"\\n"
            + "          ,\\\"partition\\\" : <partition_number>\\n" 
            + "          ,\\\"offset\\\" : <offset_number>             <--- the offset attribute is optional \\n" 
            + "        },\\n" 
            + "        ...\\n" 
            + "      ]\\n"  
            + "    }\\n"
            + "\\n"
            + "The `offset` element is optional. It specifies the offset of the first record to consume from "
            + "the topic partition. This works as follows: "
            + "\\n"
            + " * To seek to the beginning of a topic-partition, set the value of the offset to `-2.`\\n"
            + " * To seek to the end of a topic-partition, set the value of the offset attribute to `-1.`\\n"
            + " * To start fetching from the default position, omit the offset attribute or set the value of the offset to `-3`\\n"
            + " * Any other value will cause the operator to seek to that offset value. If that value does not exist, then the operator will use the "
            + "`auto.offset.reset` policy to determine where to begin reading messages from.\\n"
            + "\\n"
            + "The following types and convenience functions are available to aid in creating the JSON string: \\n"
            + "\\n"
            + "* `type Control.TopicPartition = rstring topic, int32 partition;`\\n"
            + "* `type Control.TopicPartitionOffset = rstring topic, int32 partition, int64 offset;`\\n"
            + "* `rstring createMessageRemoveTopicPartition (rstring topic, int32 partition);`\\n" 
            + "* `rstring createMessageAddTopicPartition (rstring topic, int32 partition, int64 offset);`\\n" 
            + "* `rstring createMessageAddTopicPartition (list<Control.TopicPartitionOffset> topicPartitionsToAdd);`\\n" 
            + "* `rstring createMessageAddTopicPartition (list<Control.TopicPartition> topicPartitionsToAdd);`\\n"
            + "* `rstring createMessageRemoveTopicPartition (rstring topic, int32 partition);`\\n" 
            + "* `rstring createMessageRemoveTopicPartition (list<Control.TopicPartition> topicPartitionsToRemove);`\\n"
            + "\\n"
            + "**Important Note:** This input port must not receive a final punctuation. Final markers are automatically "
            + "forwarded causing downstream operators close their input ports. When this input port receives a final marker, "
            + "it will stop fetching Kafka messages and stop submitting tuples.", 
            cardinality = 1, optional = true, controlPort = true)})
@OutputPorts({
    @OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Free) })
public class MessageHubConsumerOperator extends AbstractKafkaConsumerOperator {
    private static final Logger logger = Logger.getLogger(MessageHubConsumerOperator.class);

    private String credentialsFilename = null;
    private String credentials = null;
    private ServiceCredentialsUtil credentialsUtil = null;

    /**
     * This method hides the parameter 'staticGroupMember' from the base class 
     * as static group membership is not (yet) supported in event streams.
     * @see com.ibm.streamsx.kafka.operators.AbstractKafkaConsumerOperator#setStaticGroupMember(boolean)
     */
    @Override
    public void setStaticGroupMember (boolean staticGrpMember) {
    }

    @Parameter (optional = true, name = "credentials", description = SplDoc.PARAM_CREDENTIALS)
    public void setCredentials (String credentials) {
        this.credentials = credentials;
    }

    @Parameter(optional = true, name = "messageHubCredentialsFile", description = SplDoc.PARAM_MESSAGE_HUB_CREDS_FILE_DEPRECATED)
    public void setMessageHubCredsFile (String filename) {
        this.credentialsFilename = filename;
    }

    @Parameter(optional = true, name="credentialsFile", description = SplDoc.PARAM_CREDS_FILE)
    public void setCredsFile (String filename) {
        this.credentialsFilename = filename;
    }


    @ContextCheck (compile = true, runtime = false)
    public static void checkCredentialsFileParamDeprecation (OperatorContextChecker checker) {
        Set<String> paramNames = checker.getOperatorContext().getParameterNames();
        if (paramNames.contains ("messageHubCredentialsFile")) {
            System.err.println ("The parameter 'messageHubCredentialsFile' has been deprecated and may be removed in future versions. "
                    + "Please use 'credentialsFile' instead.");
            checker.checkExcludedParameters ("messageHubCredentialsFile", "credentialsFile");
        }
    }


    @Override
    protected void loadProperties() throws Exception {
        if (this.credentialsUtil == null) {
            this.credentialsUtil = new ServiceCredentialsUtil (this, this.credentials, this.appConfigName, this.credentialsFilename, getKafkaProperties());
        }
        credentialsUtil.loadPropertiesFromParameterOrFile();
        // collect Kafka properties
        // super.loadProperties reads 1. from properties file, 2. from app config (where the overwritten method is invoked)
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
            + "The **MessageHubConsumer** operator is used to consume records from the IBM Event Streams cloud service. "
            + ""
            + "The standard use patterns for the MessageHubConsumer operator are described in the "
            + "[https://ibmstreams.github.io/streamsx.messagehub/docs/user/overview/|overview] of the user documentation.\\n"
            + "\\n"
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
            + "This section outlines different options for enabling the **MessageHubConsumer** operator to connect to "
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
            + "Users only need to specify the topic(s) that they wish to consume messages from (set via the **topic** parameter).\\n"
            + "\\n"
            + "# Additional Operator Details\\n"
            + "\\n"
            + "# Kafka Properties\\n"
            + "\\n"
            + "The operator implements Kafka's Consumer API. As a result, it supports all "
            + "Kafka properties that are supported by the underlying API. The consumer properties "
            + "can be found in the [https://kafka.apache.org/documentation/#consumerconfigs|Apache Kafka documentation]. "
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
            + "# Kafka consumer properties that are setup or adjusted by the operator\\n"
            + "\\n"
            + KafkaSplDoc.CONSUMER_DEFAULT_AND_ADJUSTED_PROPERTIES
            + "\\n"
            + KafkaSplDoc.CONSUMER_AUTOMATIC_DESERIALIZATION
            + "\\n"
            + KafkaSplDoc.CONSUMER_EXPOSED_KAFKA_METRICS
            + "\\n"
            + KafkaSplDoc.CONSUMER_COMMITTING_OFFSETS
            + "\\n"
            + KafkaSplDoc.CONSUMER_KAFKA_GROUP_MANAGEMENT
            + "\\n"
            + KafkaSplDoc.CONSUMER_CHECKPOINTING_CONFIG
            + "\\n"
            + KafkaSplDoc.CONSUMER_RESTART_BEHAVIOUR
            + "\\n"
            + KafkaSplDoc.CONSUMER_CONSISTENT_REGION_SUPPORT
            + "\\n"
            + "# Error Handling\\n"
            + "\\n"
            + "Many exceptions thrown by the underlying Kafka API are considered fatal. In the event that Kafka throws "
            + "an exception, the operator will restart.\\n";
}
