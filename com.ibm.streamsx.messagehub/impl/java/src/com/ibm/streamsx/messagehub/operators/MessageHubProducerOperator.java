package com.ibm.streamsx.messagehub.operators;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator;
import com.ibm.streamsx.kafka.operators.KafkaSplDoc;
import com.ibm.streamsx.kafka.properties.KafkaOperatorProperties;
import com.ibm.streamsx.messagehub.operators.utils.MessageHubOperatorUtil;

@PrimitiveOperator(name = "MessageHubProducer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubProducerOperator.DESC)
@Icons(location16 = "icons/MessageHubProducer_16.png", location32 = "icons/MessageHubProducer_32.png")
@Libraries({"opt/downloaded/*", "impl/lib/*"})
@InputPorts({ @InputPortSet(description = "Port that consumes tuples", cardinality = 1, optional = false) })
public class MessageHubProducerOperator extends AbstractKafkaProducerOperator {

    private static final Logger logger = Logger.getLogger(MessageHubProducerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH; //$NON-NLS-1$
    private boolean appConfigRequired = false;

    @Parameter(optional = true, name="messageHubCredentialsFile", description="Specifies the name of the file that contains "
            + "the complete Event Streams service credentials in JSON format. If not specified, this parameter will "
            + "attempt to load the credentials from the file `etc/messagehub.json`. A relative path is always "
            + "interpreted as relative to the *application directory* of the Streams application.")
    public void setMessageHubCredsFile(String messageHubCredsFile) {
        this.messageHubCredsFile = messageHubCredsFile;
    }


    @Override
    protected void loadProperties() throws Exception {
        final KafkaOperatorProperties credsFileProps = MessageHubOperatorUtil.loadMessageHubCredsFromFile(getOperatorContext(), convertToAbsolutePath (messageHubCredsFile));
        if (credsFileProps == null || credsFileProps.isEmpty()) {
            logger.info ("Could not read service credentials from properties file; requiring an App Config.");
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
        // load more (Kafka) properties from the app config, but remove 'messagehub.creds' property, which is the complete JSON
        super.loadFromAppConfig();
        getKafkaProperties().remove(MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME);
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
            + "This section outlines different options for enabling the **MessageHubProducer** operator to connet to "
            + "the IBM Event Streams cloud service. "
            + "Any of the following options can be used to configure the operator for connecting to IBM Cloud. \\n"
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_APP_CONFIG_PROPERTY
            + "\\n"
            + SplDoc.SAVE_CREDENTIALS_IN_FILE
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
            + "[https://www.ibm.com/support/knowledgecenter/SSCRJU_4.2.1/com.ibm.streams.admin.doc/doc/"
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
            + KafkaSplDoc.CHECKPOINTING_CONFIG
            + "\\n"
            + KafkaSplDoc.PRODUCER_CONSISTENT_REGION_SUPPORT
            + "\\n"
            + "**Transactional message delivery is now supported by all plans of the Event Streams cloud service.**"
            + "\\n"
            + "\\n"
            + "# Error Handling\\n"
            + "\\n"
            + "Many exceptions thrown by the underlying Kafka API are considered fatal. In the event "
            + "that Kafka throws an exception, the operator will restart. Some exceptions can be "
            + "retried, such as those that occur due to network error. Users are encouraged "
            + "to set the KafkaProducer `retries` property to a value greater than 0 to enable the producer's "
            + "retry mechanism.";
}
