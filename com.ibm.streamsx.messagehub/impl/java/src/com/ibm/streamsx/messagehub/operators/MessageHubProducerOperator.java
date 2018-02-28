package com.ibm.streamsx.messagehub.operators;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator;
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
    		+ "the complete Message Hub credentials JSON. If not specified, this parameter will "
    		+ "attempt to load the credentials from the file `etc/messagehub.json`.")
    public void setMessageHubCredsFile(String messageHubCredsFile) {
        this.messageHubCredsFile = messageHubCredsFile;
    }

    /**
     * Not annotated as Parameter like the base class to hide the parameter `consistentRegionPolicy` in this operator.
     * MessageHub is on Kafka 0.10.2.1 that does not support transactions. Avoid using parameter consistentRegionPolicy: transactional this way.
     * The method has an empty body.
     * @see com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator#setConsistentRegionPolicy(com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator.ConsistentRegionPolicy)
     */
    @Override
    public void setConsistentRegionPolicy (AbstractKafkaProducerOperator.ConsistentRegionPolicy consistentRegionPolicy) {
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
        }
        super.loadProperties();
    }

    @Override
    protected void loadFromAppConfig() throws Exception {
        final KafkaOperatorProperties appCfgProps = MessageHubOperatorUtil.loadMessageHubCredsFromAppConfig(getOperatorContext(), appConfigName);
        if (appConfigRequired && (appCfgProps == null || appCfgProps.isEmpty())) {
            final String msg = "Message Hub credentials not found in properties file nor in an Application Configuration";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        getKafkaProperties().putAllIfNotPresent(appCfgProps);
        super.loadFromAppConfig();
    }
    
    public static final String DESC = ""
    		+ "The **MessageHubProducer** operator is used to consume records from the IBM Cloud Message Hub service. "
    		+ "The operator has been designed to make connectivity to the service as simple as possible. "
    		+ "This is achieved in a number of different ways, from having default values for the **appConfigName** parameter "
    		+ "to allowing the user to copy/paste the raw Message Hub Credentials JSON into either an application configuration "
    		+ "property or a file.\\n"
    		+ "\\n"
    		+ "The following table lists the default values that have been set by this operator for a couple of parameters: \\n" + 
    		"\\n" + 
    		"---\\n" + 
    		"| Parameter | Default Value | Description |\\n" + 
    		"|===|\\n" + 
    		"| **appConfigName** | `messagehub` | Users can choose to place the raw MesageHub Credentials JSON in a property called `messagehub.creds` in an application configuration called `messagehub`. The operator will extract the information needed to connect to Message Hub. |\\n" + 
    		"|---|\\n" + 
    		"| **messageHubCredentialsFile** | `etc/messagehub.json` | Users can paste the raw Message Hub Credentials JSON into a file pointed to by this parameter. The operator will extract the information needed to connect to Message Hub. By default, the operator will look for a file called `etc/messagehub.json`. |\\n" + 
    		"---\\n" + 
    		"\\n" +  
    		"\\n" + 
    		"# Setup\\n" + 
    		"\\n" + 
    		"This section outlines different options for enabling the **MessageHubProducer** operator to connet to IBM Cloud Message Hub. "
    		+ "Any of the following options can be used to configure the operator for connecting to IBM Cloud. \\n" + 
    		"\\n" + 
    		"**Save Credentials in Application Configuration Property**\\n" + 
    		"\\n" + 
    		"With this option, users can copy their Message Hub Credentials JSON from the Message Hub service and "
    		+ "store it in an application configuration property called `messagehub.creds`. When the operator starts, "
    		+ "it will look for that property and extract the information needed to connect. "
    		+ "The following steps outline how this can be done: \\n" + 
    		"\\n" + 
    		" 1. Create an application configuration called `messagehub`.\\n" + 
    		" 2. Create a property in the `messagehub` application configuration *named* `messagehub.creds`.\\n" + 
    		"   * The *value* of the property should be the raw Message Hub Credentials JSON\\n" + 
    		" 3. The operator will automatically look for an application configuration named `messagehub` and will extract "
    		+ "the information needed to connect. Users only need to specify the topic(s) that they wish to consume messages "
    		+ "from (set via the **topic** parameter).\\n" + 
    		"\\n" + 
    		"**NOTE:** Users can specify a different application configuration name by setting the **appConfigName** "
    		+ "parameter. The operator will still look for a property called `messagehub.creds` containing the "
    		+ "Message Hub Credentials JSON. \\n" + 
    		"\\n" + 
    		"\\n" + 
    		"**Save Credentials in File**\\n" + 
    		"\\n" + 
    		"With this option, users can copy their Message Hub credentials JSON from the Message Hub service and store it in a "
    		+ "file called `messagehub.json`. When the operator starts up it will read the credentials from that file and "
    		+ "extract the information needed to connect. The following steps outline how this can be done: \\n" + 
    		"\\n" + 
    		" 1. Create a file called `messagehub.json` in the `<application_directory>/etc/` directory. \\n" + 
    		" 2. Paste the Message Hub Credentials JSON into this file and save it. \\n" + 
    		" 3. The operator will automatically look for the file `<application_directory>/etc/messagehub.json` and "
    		+ "will extract the information needed to connect. Users only need to specify the topic(s) "
    		+ "that they wish to consume messages from (set via the **topic** parameter).\\n" + 
    		"\\n" + 
    		"**NOTE:** Users can use the **messageHubCredentialsFile** parameter to specify a different file containing the Message Hub Credentials JSON.\\n" + 
    		"\\n" + 
    		"\\n" + 
    		"# Additional Operator Details\\n" + 
    		"\\n" + 
    		"The Message Hub Toolkit wraps the `KafkaConsumer` and `KafkaProducer` operators from a specific version of the Kafka Toolkit. "
    		+ "This implies that all of the functionality and restrictions provided by the Kafka Toolkit are inherited by the Message Hub Toolkit.\\n" + 
    		"\\n" + 
    		"This version of the Message Hub Toolkit wraps **Kafka Toolkit v1.x**. It is recommended that users review "
    		+ "the Kafka Toolkit documentation for additional information on supported functionality. "
    		+ "The Kafka Toolkit documentation can be found here: "
    		+ "[ https://ibmstreams.github.io/streamsx.kafka/ | Kafka Toolkit].\\n" + 
    		"\\n";
}
