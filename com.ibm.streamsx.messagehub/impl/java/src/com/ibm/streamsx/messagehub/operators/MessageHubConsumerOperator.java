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

@PrimitiveOperator(name = "MessageHubConsumer", namespace = "com.ibm.streamsx.messagehub")
@Icons(location16 = "icons/MessageHubConsumer_16.png", location32 = "icons/MessageHubConsumer_32.png")
@Libraries({"opt/downloaded/*"})
@OutputPorts({
    @OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class MessageHubConsumerOperator extends AbstractKafkaConsumerOperator {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MessageHubConsumerOperator.class);

    private String messageHubCredsFile = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH;

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
}
