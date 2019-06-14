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
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator;
import com.ibm.streamsx.kafka.operators.KafkaSplDoc;
import com.ibm.streamsx.messagehub.operators.utils.ServiceCredentialsUtil;

@PrimitiveOperator(name = "MessageHubProducer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubProducerOperator.DESC)
@Icons(location16 = "icons/MessageHubProducer_16.png", location32 = "icons/MessageHubProducer_32.png")
@Libraries({"opt/downloaded/*", "impl/lib/*"})
@InputPorts({ @InputPortSet(description = "Port that consumes tuples", cardinality = 1, optional = false) })
public class MessageHubProducerOperator extends AbstractKafkaProducerOperator {

    private static final Logger logger = Logger.getLogger(MessageHubProducerOperator.class);

    private String credentialsFilename = null;
    private String credentials = null;
    private ServiceCredentialsUtil credentialsUtil = null;

    @Parameter (optional = true, name = "credentials", description = SplDoc.PARAM_CREDENTIALS)
    public void setCredentials (String credentials) {
        this.credentials = credentials;
    }

    @Parameter(optional = true, name = "messageHubCredentialsFile", description = SplDoc.PARAM_MESSAGE_HUB_CREDS_FILE_DEPRECATED)
    public void setMessageHubCredsFile (String filename) {
        this.credentialsFilename = filename;
    }

    @Parameter(optional = true, name = "credentialsFile", description = SplDoc.PARAM_CREDS_FILE)
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
            + "1. credentials stored in a file, also when the default filename `etc/eventstreams.json` or the deprecated default `etc/messagehub.json` is used (application config is ignored)\\n"
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
            + "that Kafka throws an exception, the operator will restart. Some exceptions can be "
            + "retried, such as those that occur due to network error. Users are encouraged "
            + "to set the KafkaProducer `retries` property to a value greater than 0 to enable the producer's "
            + "retry mechanism.";
}
