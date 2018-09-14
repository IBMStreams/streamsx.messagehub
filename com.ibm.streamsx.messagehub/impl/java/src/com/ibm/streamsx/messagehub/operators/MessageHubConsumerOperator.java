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
            + " * To seek to the beginning of a topic-partition, set the value of the offset to `-1.`\\n"
            + " * To seek to the end of a topic-partition, set the value of the offset attribute to `-2.`\\n"
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
            + "          \\\"topic\\\" : \\\"topic-name\\\",\\n"
            + "          \\\"partition\\\" : <partition_number>,\\n" 
            + "          \\\"offset\\\" : <offset_number>\\n" 
            + "        },\\n" 
            + "        ...\\n" 
            + "      ]\\n"  
            + "    }\\n"
            + "\\n"
            + "The following convenience functions are available to aid in creating the messages: \\n"
            + "\\n"
            + " * `rstring addTopicPartitionMessage(rstring topic, int32 partition, int64 offset);` \\n" 
            + "\\n"
            + " * `rstring addTopicPartitionMessage(list<tuple<rstring topic, int32 partition, int64 offset>> topicPartitionsToAdd);` \\n" 
            + "\\n"
            + " * `rstring removeTopicPartitionMessage(rstring topic, int32 partition);` \\n" 
            + "\\n"  
            + " * `rstring removeTopicPartitionMessage(list<tuple<rstring topic, int32 partition>> topicPartitionsToRemove);`", 
            cardinality = 1, optional = true)})
@OutputPorts({
    @OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class MessageHubConsumerOperator extends AbstractKafkaConsumerOperator {
    private static final Logger logger = Logger.getLogger(MessageHubConsumerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH;
    private boolean appConfigRequired = false;

    @Parameter(optional = true, name="messageHubCredentialsFile", description="Specifies the name of the file that contains "
            + "the complete Message Hub credentials JSON. If not specified, this parameter will "
            + "attempt to load the credentials from the file `etc/messagehub.json`. A relative path is always "
            + "interpreted as relative to the *application directory* of the Streams application.")
    public void setMessageHubCredsFile(String messageHubCredsFile) {
        this.messageHubCredsFile = messageHubCredsFile;
    }

    @Override
    protected void loadProperties() throws Exception {
        final KafkaOperatorProperties credsFileProps = MessageHubOperatorUtil.loadMessageHubCredsFromFile(getOperatorContext(), convertToAbsolutePath (messageHubCredsFile));
        if (credsFileProps == null || credsFileProps.isEmpty()) {
            logger.info ("Could not read Message Hub credentials from properties file; requiring an App Config.");
            appConfigRequired  = true;
        }
        else {
            getKafkaProperties().putAllIfNotPresent(credsFileProps);
            logger.info ("kafka properties derived from the content of the credentials file " + messageHubCredsFile 
                    + ": " + credsFileProps.keySet());
        }
        // super.loadProperties reads 1. from properties file, 2. from app config (where the overwritten method is invoked)
        super.loadProperties();
        //        for (Object key: getKafkaProperties().keySet()) System.out.println (key + " = '" + getKafkaProperties().get(key) + "'");
    }

    @Override
    protected void loadFromAppConfig() throws Exception {
        final String appCfgName = appConfigName == null? MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_APP_CONFIG_NAME: appConfigName;
        final KafkaOperatorProperties appCfgProps = MessageHubOperatorUtil.loadMessageHubCredsFromAppConfig(getOperatorContext(), appCfgName);
        if (appConfigRequired && (appCfgProps == null || appCfgProps.isEmpty())) {
            final String msg = "Message Hub credentials not found in properties file nor in an Application Configuration";
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
    // TODO: refactor the documentation by re-using parts from the Kafka toolkit operator
    public static final String DESC = ""
            + "The **MessageHubConsumer** operator is used to consume records from the IBM Cloud Message Hub service. "
            + "The operator has been designed to make connectivity to the service as simple as possible. "
            + "This is achieved in a number of different ways, from having default values for the **appConfigName** parameter "
            + "to allowing the user to copy/paste the raw Message Hub Credentials JSON into either an application configuration "
            + "property or a file.\\n"
            + "\\n"
            + "The following table lists the default values that have been set by this operator for a couple of parameters: \\n"
            + "\\n" 
            + SplDoc.DEFAULT_CREDENTIAL_PARAMETERS
            + "\\n"
            + "\\n"
            + "# Setup\\n"
            + "\\n"
            + "This section outlines different options for enabling the **MessageHubConsumer** operator to connet to IBM Cloud Message Hub. "
            + "Any of the following options can be used to configure the operator for connecting to IBM Cloud. \\n"
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_FILE
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
            // TODO extract into extra text snippet in Kafka toolkit
            + "---\\n"
            + "| Property Name | Default Value |\\n"
            + "|===|\\n"
            + "| client.id | Generated ID in the form: `C-J<JobId>-<operator name>` |\\n"
            + "|---|\\n"
            + "| group.id | Randomly generated ID in the form: `group-<random_string>` |\\n"
            + "|---|\\n"
            + "| key.deserializer | See **Automatic deserialization** section below |\\n"
            + "|---|\\n"
            + "| value.deserializer | See **Automatic deserialization** section below |\\n"
            + "|---|\\n"
            + "| auto.commit.enable | adjusted to `false` |\\n"
            + "|---|\\n"
            + "| max.poll.interval.ms | adjusted to a minimum of *3 \\\\* max (reset timeout, drain timeout)* when in consistent region, 300000 otherwise |\\n"
            + "|---|\\n"
            + "| metadata.max.age.ms | adjusted to a maximum of 2000 |\\n"
            + "|---|\\n"
            + "| session.timeout.ms | adjusted to a minimum of 120000 |\\n"
            + "|---|\\n"
            + "| request.timeout.ms | adjusted to a minimum of 125000 |\\n"
            + "---\\n"
            + "\\n"
            + ""
            // TODO: extract
            + "# Automatic Deserialization\\n"
            + "\\n"
            + "The operator will automatically select the appropriate deserializers for the key and message "
            + "based on their types. The following table outlines which deserializer will be used given a "
            + "particular type: \\n"
            + "\\n"
            + "---\\n"
            + "| Deserializer | SPL Types |\\n"
            + "|===|\\n"
            + "| org.apache.kafka.common.serialization.StringDeserializer | rstring |\\n"
            + "|---|\\n"
            + "| org.apache.kafka.common.serialization.IntegerDeserializer | int32, uint32 |\\n"
            + "|---|\\n"
            + "| org.apache.kafka.common.serialization.LongDeserializer | int64, uint64 |\\n"
            + "|---|\\n"
            + "| org.apache.kafka.common.serialization.FloatDeserializer | float32 |\\n"
            + "|---|\\n"
            + "| org.apache.kafka.common.serialization.DoubleDeserializer | float64 |\\n"
            + "|---|\\n"
            + "| org.apache.kafka.common.serialization.ByteArrayDeserializer | blob | \\n"
            + "---\\n"
            + "\\n"
            + "These deserializers are wrapped by extensions that catch exceptions of type "
            + "`org.apache.kafka.common.errors.SerializationException` to allow the operator to skip "
            + "over malformed messages. The used extensions do not modify the actual deserialization "
            + "function of the given base deserializers from the above table.\\n"
            + "\\n"
            + "Users can override this behaviour and specify which deserializer to use by setting the "
            + "`key.deserializer` and `value.deserializer` properties. \\n"
            + "\\n"
            // TODO: extract
            + "# Kafka's Group Management\\n"
            + "\\n"
            + "The operator is capable of taking advantage of [https://kafka.apache.org/documentation/#intro_consumers|Kafka's group management function]. "
            + "In order for the operator to use this functionality, the following requirements "
            + "must be met\\n"
            + "\\n"
            + "* The `group.id` consumer property must be set to define, which operators belong to a consumer group. "
            + "This can be done via property file, app option, or the **groupId** parameter.\\n"
            + "* The operator must not be configured with the optional input port.\\n"
            + "* The parameter **partition** must not be used.\\n"
            + "* **Restriction, when not in a consistent region:** The **startPosition** parameter must have the "
            + "value `Default` or must not be specified ([https://github.com/IBMStreams/streamsx.kafka/issues/94|issue 94]).\\n"
            + "\\n"
            + "The other way round, group management is inactive in following cases\\n"
            + "* when no group ID is specified, or\\n"
            + "* when the operator is configured with input port, or\\n"
            + "* when the **partition** parameter is specified, or\\n"
            + "* **only when not in consistent region**, the value of the **startPosition** is different from `Default`.\\n"
            + "\\n"
            + "In a consistent region, a consumer group must not have consumers outside of the consistent region, "
            + "for example in a different Streams job. The state of group management is indicated by the custom "
            + "metric **isGroupManagementActive**, where `1` indicates that group management is active.\\n"
            + "\\n"
            // TODO: extract snippet
            + "# Committing received Kafka messages\\n"
            + "\\n"
            + "**a) The operator is not part of a consistent region**\\n"
            + "\\n"
            + "The consumer operator commits the offsets of "
            + "those Kafka messages that have been submitted as tuples. The frequency in terms of "
            + "number of tuples can be specified with the **commitCount** parameter. This parameter has a default value of 500. "
            + "Offsets are committed asynchronously.\\n"
            + "\\n"
            + "**b) The operator is part of a consistent region**\\n"
            + "\\n"
            + "Offsets are always committed when the consistent region drains, i.e. when the region becomes a consistent state. "
            + "On drain, the consumer operator commits the offsets of those Kafka messages that have been submitted as tuples. "
            + "When the operator is in a consistent region, all auto-commit related settings via consumer properties are "
            + "ignored by the operator. The parameter **commitCount** is also ignored because the commit frequency is given "
            + "by the trigger period of the consistent region.\\n"
            + "In a consistent region, offsets are committed synchronously, i.e. the offsets are committed when the drain "
            + "processing of the operator finishes. Commit failures result in consistent region reset.\\n"
            + "\\n"
            // TODO extract
            + "# Consistent Region Support\\n"
            + "\\n"
            + "The operator "
            + "can be the start of a consistent region. Both operator driven and periodic triggering of the region "
            + "are supported. If using operator driven, the **triggerCount** parameter must be set to "
            + "indicate how often the operator should initiate a consistent region.\\n"
            + "\\n"
            + "When a group-ID is specified via the consumer property `group.id` or the **groupId** parameter, the operator "
            + "participates automatically in a consumer group defined by the group ID. A consistent region can have "
            + "multiple consumer groups.\\n"
            + "\\n"
            + "When the consumers of a consumer group rebalance the partition assignment, for example, immediately after job "
            + "submission, or when the broker node being the group's coordinator is shutdown, multiple resets of the consistent "
            + "region can occur when the consumers start up. It is recommended to set the "
            + "`maxConsecutiveResetAttempts` parameter of the `@consistent` annotation to a higher value than the default value of 5.\\n"
            + "\\n"
            + "On drain, the operator will commit offsets. The drain duration of the last drain and the maximum drain "
            + "duration in milliseconds is kept in the custom metrics **drainTimeMillis** and **drainTimeMillisMax**. "
            + "These metrics are only present when the operator participates in a consistent region.\\n"
            + "\\n"
            + "On checkpoint, the operator will save the last offset for each topic-partition that it is assigned to. In the "
            + "event of a reset, the operator will seek to the saved offset for each topic-partition and "
            + "begin consuming messages from that point."
            + "\\n"
            + "\\n"
            + "# Error Handling\\n"
            + "\\n"
            + "Many exceptions thrown by the underlying Kafka API are considered fatal. In the event that Kafka throws "
            + "an exception, the operator will restart.\\n";
}
