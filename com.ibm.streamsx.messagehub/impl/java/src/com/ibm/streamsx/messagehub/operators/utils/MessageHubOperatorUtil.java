package com.ibm.streamsx.messagehub.operators.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.log4j.Logger;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streamsx.kafka.i18n.Messages;
import com.ibm.streamsx.kafka.properties.JaasUtil;
import com.ibm.streamsx.kafka.properties.KafkaOperatorProperties;
import com.ibm.streamsx.messagehub.credentials.MessageHubCredentials;

public class MessageHubOperatorUtil {

    public static final String DEFAULT_MESSAGE_HUB_APP_CONFIG_NAME = "messagehub"; //$NON-NLS-1$
    public static final String DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME = "messagehub.creds"; //$NON-NLS-1$
    public static final String DEFAULT_MESSAGE_HUB_CREDS_FILE_PATH = "etc/messagehub.json"; //$NON-NLS-1$
    public static final Logger logger = Logger.getLogger(MessageHubOperatorUtil.class);

    public static KafkaOperatorProperties loadMessageHubCredsFromAppConfig(OperatorContext context,
            String appConfigName) throws Exception {
        if (appConfigName == null) {
            appConfigName = MessageHubOperatorUtil.DEFAULT_MESSAGE_HUB_APP_CONFIG_NAME;
            
        }
        logger.info("Attempting to load app config from: " + appConfigName); //$NON-NLS-1$

        KafkaOperatorProperties properties = new KafkaOperatorProperties();
        Map<String, String> appConfig = context.getPE().getApplicationConfiguration(appConfigName);
        logger.info ("Properties read from App Config " + appConfigName + ": " + appConfig.keySet());
        if (appConfig.isEmpty()) {
            // we have no indication whether the app config exists or not.
            logger.warn ("App Config '" + appConfigName + "' does not exist or is empty.");
        }
        if (appConfig.containsKey(DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME)) {
            String credentials = appConfig.get(DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME);
            logger.trace("Creds from app config property: " + credentials); //$NON-NLS-1$
            KafkaOperatorProperties messageHubProperties = loadFromMessageHubCreds(context, credentials);
            properties.putAllIfNotPresent(messageHubProperties);
        }
        else {
            logger.warn ("App Config '" + appConfigName + "' has no key '" + DEFAULT_MESSAGE_HUB_CREDS_PROPERTY_NAME + "' where the Message Hub credentials in JSON format are expected.");
        }

        return properties;
    }

    public static KafkaOperatorProperties loadMessageHubCredsFromFile(OperatorContext context, File messageHubCredsFile)
            throws Exception {
    	logger.info("Attempting to load properties file from: " + messageHubCredsFile);
        if (!messageHubCredsFile.exists()) {
            logger.info("Message Hub credentials file does not exist: " + messageHubCredsFile.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }
        String creds = Files.toString(messageHubCredsFile, StandardCharsets.UTF_8);
        if (creds == null || creds.trim().isEmpty()) {
            logger.warn ("Credential file " + messageHubCredsFile + " exists, but is empty");
        }
        return loadFromMessageHubCreds(context, creds);
    }

    public static KafkaOperatorProperties loadFromMessageHubCreds(OperatorContext context, String credentials) {
        if (credentials == null || credentials.trim().isEmpty()) {
            return null;
        }

        logger.info ("Parsing Message Hub creds: ** NOT LOGGED **");
        logger.trace ("Message Hub creds: " + credentials);  // this exposes sensitive information
        
        KafkaOperatorProperties properties = new KafkaOperatorProperties();
        Gson gson = new Gson();
        MessageHubCredentials messageHubCreds;
        try {
            messageHubCreds = gson.fromJson(credentials, MessageHubCredentials.class);
        } catch (JsonSyntaxException e) {
            String msg = Messages.getString("INVALID_MESSAGEHUB_JSON_CREDS", credentials); //$NON-NLS-1$
            logger.error(msg);
            throw new RuntimeException(msg, e);
        }

        // add bootstrap servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StringUtils.join(messageHubCreds.getKafkaBrokersSasl(), ",")); //$NON-NLS-1$

        // add SASL JAAS property
        String value = JaasUtil.getSaslJaasPropertyValue(messageHubCreds.getUser(), messageHubCreds.getPassword());
        properties.put(JaasUtil.SASL_JAAS_PROPERTY, value);

        // for debugging purpose, trace the messagehub username
        logger.info("message hub instance user = " + messageHubCreds.getUser());
        // add SSL properties
        properties.put("security.protocol", "SASL_SSL"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("sasl.mechanism", "PLAIN"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("ssl.protocol", "TLSv1.2"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("ssl.truststore.type", "JKS"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("ssl.enabled.protocols", "TLSv1.2"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("ssl.endpoint.identification.algorithm", "HTTPS"); //$NON-NLS-1$ //$NON-NLS-2$

        // for logging, we create a temporary set of properties, in which we replace SASL_JAAS_PROPERTIES by stars
        KafkaOperatorProperties logProps = new KafkaOperatorProperties();
        logProps.putAll(properties);
        if (logProps.containsKey(JaasUtil.SASL_JAAS_PROPERTY)) logProps.put (JaasUtil.SASL_JAAS_PROPERTY, "**********");
        logger.info ("Properties from Message Hub credentials: " + logProps); //$NON-NLS-1$
        
        return properties;
    }
}
