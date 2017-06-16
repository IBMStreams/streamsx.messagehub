package com.ibm.streamsx.messagehub.operators;

import org.apache.log4j.Logger;

import com.ibm.streams.operator.model.Icons;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.kafka.operators.AbstractKafkaProducerOperator;
import com.ibm.streamsx.messagehub.operators.utils.MessageHubOperatorUtil;

@PrimitiveOperator(name = "MessageHubProducer", namespace = "com.ibm.streamsx.messagehub", description=MessageHubProducerOperator.DESC)
@Icons(location16 = "icons/MessageHubProducer_16.png", location32 = "icons/MessageHubProducer_32.png")
@Libraries({"opt/downloaded/*"})
@InputPorts({ @InputPortSet(description = "Port that consumes tuples", cardinality = 1, optional = false) })
public class MessageHubProducerOperator extends AbstractKafkaProducerOperator {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MessageHubProducerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH; //$NON-NLS-1$

    @Parameter(optional = true)
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
    
    public static final String DESC = "The MessageHubProducer operator is used to write messages to the BlueMix MessageHub service.";
}
