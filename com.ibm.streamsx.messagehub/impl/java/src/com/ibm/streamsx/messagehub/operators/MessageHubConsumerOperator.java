package com.ibm.streamsx.messagehub.operators;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.ibm.streamsx.kafka.operators.AbstractKafkaConsumerOperator;
import com.ibm.streamsx.messagehub.operators.utils.MessageHubOperatorUtil;

@PrimitiveOperator(name = "MessageHubConsumer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubConsumerOperator.DESC)
@Icons(location16 = "icons/MessageHubConsumer_16.png", location32 = "icons/MessageHubConsumer_32.png")
@Libraries({"opt/downloaded/*"})
@OutputPorts({
    @OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class MessageHubConsumerOperator extends AbstractKafkaConsumerOperator {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MessageHubConsumerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH;

    @Parameter(optional = true, name="messageHubCredentialsFile", description="Specifies the name of the file that contains "
    		+ "the complete MessageHub credentials JSON. If not specified, this parameter will "
    		+ "attempt to load the credentials from the file `etc/messagehub.json`.")
    public void setMessageHubCredsFile(String messageHubCredsFile) {
        this.messageHubCredsFile = messageHubCredsFile;
    }

    @Override
    protected void loadProperties() throws Exception {
        getKafkaProperties().putAllIfNotPresent(MessageHubOperatorUtil.loadMessageHubCredsFromFile(getOperatorContext(),
                convertToAbsolutePath(messageHubCredsFile)));
        super.loadProperties();
    }

    @Override
    protected void loadFromAppConfig() throws Exception {
        getKafkaProperties().putAllIfNotPresent(
                MessageHubOperatorUtil.loadMessageHubCredsFromAppConfig(getOperatorContext(), appConfigName));
        super.loadFromAppConfig();
    }
    
    public static final String DESC = ""
    		+ "The **MessageHubConsumer** operator is used to consume records from the Bluemix MessageHub service. "
    		+ "The operator has been designed to make connectivity to the service as simple as possible. "
    		+ "This is achieved in a number of different ways, from having default values for the **appConfigName** parameter "
    		+ "to allowing the user to copy/paste the raw MessageHub Credentials JSON into either an application configuration "
    		+ "property or a file.\\n"
    		+ "\\n"
    		+ "The following table lists the default values that have been set by this operator for a couple of parameters: \\n" + 
    		"\\n" + 
    		"---\\n" + 
    		"| Parameter | Default Value | Description |\\n" + 
    		"|===|\\n" + 
    		"| **appConfigName** | `messagehub` | Users can choose to place the raw MesageHub Credentials JSON in a property called `messagehub.creds` in an application configuration called `messagehub`. The operator will extract the information needed to connect to MessageHub. |\\n" + 
    		"|---|\\n" + 
    		"| **messageHubCredentialsFile** | `etc/messagehub.json` | Users can paste the raw MessageHub Credentials JSON into a file pointed to by this parameter. The operator will extract the information needed to connect to MessageHub. By default, the operator will look for a file called `etc/messagehub.json`. |\\n" + 
    		"---\\n" + 
    		"\\n" +  
    		"\\n" + 
    		"# Setup\\n" + 
    		"\\n" + 
    		"This section outlines different options for enabling the **MessageHubConsumer** operator to connet to Bluemix MessageHub. "
    		+ "Any of the following options can be used to configure the operator for connecting to Bluemix. \\n" + 
    		"\\n" + 
    		"**Save Credentials in Application Configuration Property**\\n" + 
    		"\\n" + 
    		"With this option, users can copy their MessageHub Credentials JSON from the MessageHub service and "
    		+ "store it in an application configuration property called `messagehub.creds`. When the operator starts, "
    		+ "it will look for that property and extract the information needed to connect. "
    		+ "The following steps outline how this can be done: \\n" + 
    		"\\n" + 
    		" 1. Create an application configuration called `messagehub`.\\n" + 
    		" 2. Create a property in the `messagehub` application configuration *named* `messagehub.creds`.\\n" + 
    		"   * The *value* of the property should be the raw MessageHub Credentials JSON\\n" + 
    		" 3. The operator will automatically look for an application configuration named `messagehub` and will extract "
    		+ "the information needed to connect. Users only need to specify the topic(s) that they wish to consume messages "
    		+ "from (set via the **topic** parameter).\\n" + 
    		"\\n" + 
    		"**NOTE:** Users can specify a different application configuration name by setting the **appConfigName** "
    		+ "parameter. The operator will still look for a property called `messagehub.creds` containing the "
    		+ "MessageHub Credentials JSON. \\n" + 
    		"\\n" + 
    		"\\n" + 
    		"**Save Credentials in File**\\n" + 
    		"\\n" + 
    		"With this option, users can copy their MessageHub credentials JSON from the MessageHub service and store it in a "
    		+ "file called `messagehub.json`. When the operator starts up it will read the credentials from that file and "
    		+ "extract the information needed to connect. The following steps outline how this can be done: \\n" + 
    		"\\n" + 
    		" 1. Create a file called `messagehub.json` in the `<application_directory>/etc/` directory. \\n" + 
    		" 2. Paste the MessageHub Credentials JSON into this file and save it. \\n" + 
    		" 3. The operator will automatically look for the file `<application_directory>/etc/messagehub.json` and "
    		+ "will extract the information needed to connect. Users only need to specify the topic(s) "
    		+ "that they wish to consume messages from (set via the **topic** parameter).\\n" + 
    		"\\n" + 
    		"**NOTE:** Users can use the **messageHubCredentialsFile** parameter to specify a different file containing the MessageHub Credentials JSON.\\n" + 
    		"\\n" + 
    		"\\n" + 
    		"# Additional Operator Details\\n" + 
    		"\\n" + 
    		"The MessageHub Toolkit wraps the `KafkaConsumer` and `KafkaProducer` operators from a specific version of the Kafka Toolkit. "
    		+ "This implies that all of the functionality and restrictions provided by the Kafka Toolkit are inherited by the MessageHub Toolkit.\\n" + 
    		"\\n" + 
    		"This version of the MessageHub Toolkit wraps **Kafka Toolkit v1.x**. It is recommended that users review "
    		+ "the Kafka Toolkit documentation for additional information on supported functionality. "
    		+ "The Kafka Toolkit documentation can be found here: "
    		+ "[ https://ibmstreams.github.io/streamsx.kafka/docs/v0.1.0.0/spldoc/html/index.html | Kafka Toolkit v1.x Documentation ].\\n" + 
    		"\\n";
}
