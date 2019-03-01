package com.ibm.streamsx.messagehub.operators;

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
import com.ibm.streamsx.kafka.operators.AbstractKafkaConsumerOperator;
import com.ibm.streamsx.kafka.operators.KafkaSplDoc;
import com.ibm.streamsx.kafka.properties.KafkaOperatorProperties;
import com.ibm.streamsx.messagehub.operators.utils.MessageHubOperatorUtil;

@PrimitiveOperator(name = "MessageHubConsumer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubConsumerOperator.DESC)
@Icons(location16 = "icons/MessageHubConsumer_16.png", location32 = "icons/MessageHubConsumer_32.png")
@Libraries({"opt/downloaded/*", "impl/lib/*"})
@InputPorts({
    @InputPortSet(description = "This port is used to specify the topic-partition offsets that the consumer should begin reading messages from. When this "
            + "port is specified, the operator will ignore the `topic`, `partition` and `startPosition` parameters. The operator will only begin "
            + "consuming messages once a tuple is received on this port. Each tuple received on this port will cause the operator to "
            + "seek to the offsets for the specified topic-partitions. This works as follows: "
            + "\\n"
            + " * To seek to the beginning of a topic-partition, set the value of the offset to `-2.`\\n"
            + " * To seek to the end of a topic-partition, set the value of the offset attribute to `-1.`\\n"
            + " * To start fetching from the default position, omit the offset attribute or set the value of the offset to `-3`\\n"
            + " * Any other value will cause the operator to seek to that offset value. If that value does not exist, then the operator will use the "
            + "`auto.offset.reset` policy to determine where to begin reading messages from.\\n"
            + "\\n"
            + "This input port must contain a single `rstring` attribute. In order to add or remove a topic partition, the attribute must contain "
            + "a JSON string in the following format: \\n"
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
            + "The following types and convenience functions are available to aid in creating the messages: \\n"
            + "\\n"
            + "* `type Control.TopicPartition = rstring topic, int32 partition;`\\n"
            + "* `type Control.TopicPartitionOffset = rstring topic, int32 partition, int64 offset;`\\n"
            + "* `rstring addTopicPartitionMessage (rstring topic, int32 partition, int64 offset);`\\n" 
            + "* `rstring addTopicPartitionMessage (rstring topic, int32 partition);`\\n" 
            + "* `rstring addTopicPartitionMessage (list<Control.TopicPartitionOffset> topicPartitionsToAdd);`\\n" 
            + "* `rstring addTopicPartitionMessage (list<Control.TopicPartition> topicPartitionsToAdd);`\\n" 
            + "* `rstring removeTopicPartitionMessage (rstring topic, int32 partition);`\\n" 
            + "* `rstring removeTopicPartitionMessage (list<Control.TopicPartition> topicPartitionsToRemove);`\\n"
            + "\\n"
            + "**Important Note:** This input port must not receive a final punctuation. Final markers are automatically "
            + "forwarded causing downstream operators close their input ports. When this input port receives a final marker, "
            + "it will stop fetching messages from event streams and stop submitting tuples.", 
            cardinality = 1, optional = true, controlPort = true)})
@OutputPorts({
    @OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class MessageHubConsumerOperator extends AbstractKafkaConsumerOperator {
    private static final Logger logger = Logger.getLogger(MessageHubConsumerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH;
    private String credentials = null;
    private boolean appConfigRequired = false;

    @Parameter (optional = true, name = "credentials", description = "Specifies the credentials of the Event Streams cloud service instance in JSON. "
            + "This parameter takes priority over a credentials file and credentials specified as property in an application configuration.")
    public void setCredentials (String credentials) {
        this.credentials = credentials;
    }

    @Parameter(optional = true, name="messageHubCredentialsFile", description="Specifies the name of the file that contains "
            + "the complete Event Streams service credentials in JSON format. If not specified, this parameter will "
            + "attempt to load the credentials from the file `etc/messagehub.json`. A relative path is always "
            + "interpreted as relative to the *application directory* of the Streams application.\\n"
            + "\\n"
            + "Credentials stored in a file take priority over credentials stored in an appclication configuration.")
    public void setMessageHubCredsFile(String messageHubCredsFile) {
        this.messageHubCredsFile = messageHubCredsFile;
    }

    @Override
    protected void loadProperties() throws Exception {
        KafkaOperatorProperties credsProps = MessageHubOperatorUtil.loadFromMessageHubCreds (this.credentials);
        if (credsProps != null) {
            getKafkaProperties().putAllIfNotPresent (credsProps);
            logger.info ("kafka properties derived from the 'credentials' parameter value: " + credsProps.keySet());
        }
        else {
            // no credentials parameter specified; try load from file
            credsProps = MessageHubOperatorUtil.loadMessageHubCredsFromFile(convertToAbsolutePath (messageHubCredsFile));
            if (credsProps != null && !credsProps.isEmpty()) {
                getKafkaProperties().putAllIfNotPresent(credsProps);
                logger.info ("kafka properties derived from the content of the credentials file " + messageHubCredsFile 
                        + ": " + credsProps.keySet());
            }
            else {
                logger.info ("Could not read service credentials from properties file; requiring an App Config.");
                appConfigRequired  = true;
            }
        }
        // collect Kafka properties
        // super.loadProperties reads 1. from properties file, 2. from app config (where the overwritten method is invoked)
        super.loadProperties();
    }

    @Override
    protected void loadFromAppConfig() throws Exception {
        final String appCfgName = appConfigName == null? MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_APP_CONFIG_NAME: appConfigName;
        final KafkaOperatorProperties appCfgProps = MessageHubOperatorUtil.loadMessageHubCredsFromAppConfig(getOperatorContext(), appCfgName);
        if (appConfigRequired && (appCfgProps == null || appCfgProps.isEmpty())) {
            final String msg = "Service credentials not found in properties file nor in an Application Configuration";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        logger.info ("kafka properties derived from App Config " + appCfgName + " (" 
                + MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME + "): "
                + appCfgProps.keySet());

        // When we are here, we might have read the properties derived from a JSON credentials file. In this case 'appConfigRequired' is false.
        // Then we put them only if not yet present.
        // If we have not read a JSON credentials file, we might already have read Kafka properties from a properties file.
        // To give the properties derived from the JSON credentials precedence,
        // we must put them, regardless of what is already present in the properties.
        if (appConfigRequired) {
            getKafkaProperties().putAll(appCfgProps);
        }
        else {
            getKafkaProperties().putAllIfNotPresent(appCfgProps);
        }
        // load more (Kafka) properties from the app config, but remove 'messagehub.creds'
        super.loadFromAppConfig();
        getKafkaProperties().remove (MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME);
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
            + "This section outlines different options for enabling the **MessageHubConsumer** operator to connet to "
            + "the IBM Event Streams cloud service. "
            + "Any of the following options can be used to configure the operator for connecting to IBM Cloud. \\n"
            + "\\n"
            + "* save the credentials in an application configuration - it is the best way to protect your sensitive data\\n"
            + "* save the credentials in a file, which is bundled with the Streams application bundle\\n"
            + "* specify the credentials as **credentials** operator parameter. Any SPL rstring expression can be specified as operator parameter, for example `getSubmissionTimeValue(\\\"credentials\\\");`.\\n"
            + "\\n"
            + "The priority of the above options is\\n"
            + "1. **credentials** operator parameter (both file and application config are ignored)\\n"
            + "1. credentials stored in a file, also when the default filename `messagehub.json` is used (application config is ignored)\\n"
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
            + KafkaSplDoc.CONSUMER_KAFKA_GROUP_MANAGEMENT
            + "\\n"
            + KafkaSplDoc.CONSUMER_COMMITTING_OFFSETS
            + "\\n"
            + KafkaSplDoc.CONSUMER_CHECKPOINTING_CONFIG
            + "\\n"
            + KafkaSplDoc.CONSUMER_CONSISTENT_REGION_SUPPORT
            + "\\n"
            + "# Error Handling\\n"
            + "\\n"
            + "Many exceptions thrown by the underlying Kafka API are considered fatal. In the event that Kafka throws "
            + "an exception, the operator will restart.\\n";
}
