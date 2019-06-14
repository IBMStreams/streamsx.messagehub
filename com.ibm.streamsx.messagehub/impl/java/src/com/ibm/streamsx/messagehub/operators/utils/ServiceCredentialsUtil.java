package com.ibm.streamsx.messagehub.operators.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streamsx.kafka.i18n.Messages;
import com.ibm.streamsx.kafka.operators.AbstractKafkaOperator;
import com.ibm.streamsx.kafka.properties.JaasUtil;
import com.ibm.streamsx.kafka.properties.KafkaOperatorProperties;
import com.ibm.streamsx.messagehub.credentials.InvalidCredentialsException;
import com.ibm.streamsx.messagehub.credentials.MessageHubCredentials;

public class ServiceCredentialsUtil {

    private static final String[] DEFAULT_APP_CONFIG_NAMES = {"eventstreams", "messagehub"}; //$NON-NLS-1$
    private static final String[] APP_CONFIG_PROPERTY_NAMES = {"eventstreams.creds", "messagehub.creds"}; //$NON-NLS-1$
    private static final String[] DEFAULT_CREDENTIALS_FILE_PATHS = {"etc/eventstreams.json", "etc/messagehub.json"}; //$NON-NLS-1$
    private static final Logger TRACE = Logger.getLogger(ServiceCredentialsUtil.class);

    private final AbstractKafkaOperator operator;
    private final String credentials;
    private final String appConfigName;
    private final String credentialsFilename;
    private final KafkaOperatorProperties operatorKafkaProps;
    private boolean appConfigRequired = false;

    /**
     * @param operator            The operator instance
     * @param credentials         JSON credentials as string
     * @param appConfigName       name of an application configuration
     * @param credentialsFilename filename of JSON credentials file
     * @param operatorKafkaProps  A reference to the KafkaOperatorProperties of the operator
     *                            The properties are modified by {@link #loadPropertiesFromParameterOrFile()}
     *                            and {@link #loadFromAppConfigCredentials()}.
     */
    public ServiceCredentialsUtil (AbstractKafkaOperator operator, String credentials, String appConfigName,
            String credentialsFilename, KafkaOperatorProperties operatorKafkaProps) {
        this.operator = operator;
        this.credentials = credentials;
        this.appConfigName = appConfigName;
        this.credentialsFilename = credentialsFilename;
        this.operatorKafkaProps = operatorKafkaProps;
    }

    /**
     * Prefixes the given path name with the PEs application directory if the path is not absolute
     * @param pathname the pathname to convert to an absolute path
     * @return An absolute File
     */
    protected File convertToAbsolutePath (String pathname) {
        File f = new File (pathname);
        if (!f.isAbsolute()) {
            File appDir = operator.getOperatorContext().getPE().getApplicationDirectory();
            TRACE.info ("extending relative path '" + pathname + "' by the '" + appDir + "' directory");
            f = new File (appDir, pathname);
        }
        return f;
    }

    /**
     * Loads Kafka properties derived from (1.) credentials given as plain string or (2.) credentials file.
     * @throws Exception
     */
    public void loadPropertiesFromParameterOrFile() throws Exception {
        KafkaOperatorProperties credsProps = loadPropertiesFromCredsString (this.credentials);
        if (credsProps != null) {
            operatorKafkaProps.putAllIfNotPresent (credsProps);
            TRACE.info ("kafka properties derived from the 'credentials' parameter value: " + credsProps.keySet());
        }
        else {
            // no credentials parameter specified; try load from file
            String[] credFilePaths = this.credentialsFilename == null? DEFAULT_CREDENTIALS_FILE_PATHS: new String[] {this.credentialsFilename};
            for (String filename: credFilePaths) {
                credsProps = loadPropertiesFromCredsFile (convertToAbsolutePath (filename));
                if (credsProps != null && !credsProps.isEmpty()) {
                    operatorKafkaProps.putAllIfNotPresent(credsProps);
                    TRACE.info ("kafka properties derived from the content of the credentials file " + filename + ": " + credsProps.keySet());
                    break;
                }
            }
            if (credsProps == null || credsProps.isEmpty()) {
                TRACE.info ("Could not read service credentials from JSON file; requiring an App Config.");
                this.appConfigRequired  = true;
            }
        }
    }


    /**
     * Loads properties from credentials of given application configuration or from one of the default app configs
     * @return A map of the used app config. The key is the name of the application config, and the value is the property name
     * @throws Exception
     */
    public Map<String, String> loadFromAppConfigCredentials() throws Exception {
        final String[] appCfgNames = this.appConfigName == null? DEFAULT_APP_CONFIG_NAMES: new String[] {this.appConfigName};
        KafkaOperatorProperties appCfgProps = null;
        Map<String, String> appCfgAndKey = new HashMap<>(1);
        StringBuilder appConfigPropertyName = new StringBuilder();
        for (String appCfgName: appCfgNames) {
            appCfgProps = loadPropertiesFromFromAppConfigCreds (operator.getOperatorContext(), appCfgName, appConfigPropertyName);
            if (appCfgProps != null && !appCfgProps.isEmpty()) {
                TRACE.info ("kafka properties derived from Application Configuration '" + appCfgName + "[" + appConfigPropertyName.toString() + "]': "
                        + appCfgProps.keySet());
                appCfgAndKey.put (appCfgName, appConfigPropertyName.toString());
                break;
            }
        }
        if (this.appConfigRequired && (appCfgProps == null || appCfgProps.isEmpty())) {
            final String msg = "Service credentials not found in properties file nor in "
                    + (appCfgNames.length == 1? ("the application configuration " + appCfgNames[0]): ("one of the application configurations " + Arrays.toString (appCfgNames)));
            TRACE.error(msg);
            throw new InvalidCredentialsException (msg);
        }

        // When we are here, we might have read the properties derived from a JSON credentials file. In this case 'appConfigRequired' is false.
        // Then we put them only if not yet present.
        // If we have not read a JSON credentials file, we might already have read Kafka properties from a properties file.
        // To give the properties derived from the JSON credentials precedence,
        // we must put them, regardless of what is already present in the properties.
        if (this.appConfigRequired) {
            // appCfgProps is NOT null and NOT empty
            TRACE.info ("Adding properties from appconfig overwriting existing properties");
            operatorKafkaProps.putAll (appCfgProps);
        }
        else {
            // appCfgProps can be null or empty
            if (appCfgProps != null && !appCfgProps.isEmpty()) {
                TRACE.info ("Adding properties from appconfig preserving existing properties");
                operatorKafkaProps.putAllIfNotPresent (appCfgProps);
            }
        }
        return appCfgAndKey;
    }

    /**
     * 
     * @param context       the operator context
     * @param appConfigName the name of the application configuration
     * @param readProperty  contains the property that has been read on success
     * @return              A new KafkaOperatorProperties with Kafka properties derived from the credentials.
     *                      When the application configuration or none of the properties exist, null is returned.
     * @throws Exception    The app config exists, has the expected key, but the value is not usable. 
     */
    private static KafkaOperatorProperties loadPropertiesFromFromAppConfigCreds (OperatorContext context,
            String appConfigName, StringBuilder readProperty) throws Exception {
        assert (appConfigName != null);
        readProperty.delete(0, readProperty.length());
        TRACE.info ("Attempting to load app config from: " + appConfigName); //$NON-NLS-1$

        Map<String, String> appConfig = context.getPE().getApplicationConfiguration (appConfigName);
        TRACE.info ("Properties read from App Config " + appConfigName + ": " + appConfig.keySet());
        if (appConfig.isEmpty()) {
            // we have no indication whether the app config exists or not.
            TRACE.warn ("App Config '" + appConfigName + "' does not exist or is empty.");
            return null;
        }
        // test for existence of properties
        String propName = null;
        for (String s: APP_CONFIG_PROPERTY_NAMES) {
            if (appConfig.containsKey (s)) {
                propName = s;
                TRACE.info ("Using key '" + s + "' from Application Configuration " + appConfigName);
                break;
            }
        }
        if (propName == null) {
            TRACE.warn ("App Config '" + appConfigName + "' has none of the keys '" + Arrays.toString(APP_CONFIG_PROPERTY_NAMES) + "' where the Event Streams credentials in JSON format are expected.");
            return null;
        }
        String credentials = appConfig.get (propName);
        if (credentials == null || credentials.trim().isEmpty()) {
            throw new InvalidCredentialsException (MessageFormat.format ("Application configuration {0}, property value {1}, is empty or only whitespace", appConfigName, propName));
        }
        TRACE.info ("Credentials from application config property '" + propName + "': " + prepareCredsForLogging (credentials)); //$NON-NLS-1$
        // throws InvalidCredentialsException or returns null
        KafkaOperatorProperties messageHubProperties = loadPropertiesFromCredsString (credentials);
        readProperty.append (propName);
        return messageHubProperties;
    }

    /**
     * Derives Kafka properties from a file with service credentials in JSON format
     * @param jsonCredsFile the credentials file
     * @return A new instance of KafkaOperator properties or null when
     * a) the file is not a File, b) the given file does not exist 
     * @throws Exception The file has suspicious length, IO error, or cannot be processed.
     */
    private static KafkaOperatorProperties loadPropertiesFromCredsFile (File jsonCredsFile)
            throws Exception {
        TRACE.info("Attempting to load properties file from: " + jsonCredsFile);
        if (!jsonCredsFile.exists() || !jsonCredsFile.isFile()) {
            TRACE.info("Event Streams credentials file does not exist or is not a file: " + jsonCredsFile.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }
        if (jsonCredsFile.length() > Integer.MAX_VALUE) {
            final String msg = MessageFormat.format ("Event Streams credentials file {0} has a suspcious length: {1}. Ignoring this file.", jsonCredsFile.getAbsolutePath(), jsonCredsFile.length());
            TRACE.error (msg); //$NON-NLS-1$
            throw new InvalidCredentialsException (msg);
        }

        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream (jsonCredsFile);
            InputStreamReader reader = new InputStreamReader (inStream, Charset.forName ("UTF-8"));
            CharBuffer buffer = CharBuffer.allocate ((int)jsonCredsFile.length());
            /*int n = */reader.read (buffer);
            buffer.flip();
            String creds = buffer.toString();
            if (creds == null || creds.trim().isEmpty()) {
                final String msg = "Credential file " + jsonCredsFile + " exists, but is empty";
                TRACE.error (msg);
                throw new InvalidCredentialsException (msg);
            }
            TRACE.info ("creds = " + prepareCredsForLogging (creds));
            return loadPropertiesFromCredsString (creds);
        }
        catch (FileNotFoundException fnf) {
            TRACE.info("Event Streams credentials file does not exist: " + jsonCredsFile.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }
        catch (IOException ioe) {
            TRACE.error (MessageFormat.format ("Event Streams credentials file {0} could not be read: {1}.", jsonCredsFile.getAbsoluteFile(), ioe.getLocalizedMessage()));
            throw new Exception (ioe);
        }
        finally {
            if (inStream != null) {
                try {
                    inStream.close();
                }
                catch (Exception e) {
                    TRACE.debug (e.getMessage());
                }
            }
        }
    }

    /**
     * Creates Kafka properties from a credentials JSON string
     * @param credentials the credentials as JSON
     * @return A new KafkaOperatorProperties instance that contains the properties
     *         derived from the credentials and others required to connect with the cloud service.
     *         Returns null when 'credentials' is null or empty.
     * @throws InvalidCredentialsException non-null or non-empty credentials cannot be converted to properties.
     */
    private static KafkaOperatorProperties loadPropertiesFromCredsString (String credentials) {
        if (credentials == null || credentials.trim().isEmpty()) {
            return null;
        }
        TRACE.info ("Parsing Event Streams creds: " + prepareCredsForLogging (credentials));
        TRACE.trace ("Event Streams creds: " + credentials);  // this exposes sensitive information
        KafkaOperatorProperties properties = new KafkaOperatorProperties();
        Gson gson = new Gson();
        MessageHubCredentials messageHubCreds;
        try {
            messageHubCreds = gson.fromJson(credentials, MessageHubCredentials.class);
            if (messageHubCreds == null) {
                throw new InvalidCredentialsException ("The credentials JSON could not be parsed.");
            }
            // throws InvalidCredentialsException:
            messageHubCreds.validate();
        } catch (JsonSyntaxException e) {
            String msg = Messages.getString("INVALID_MESSAGEHUB_JSON_CREDS", prepareCredsForLogging (credentials)); //$NON-NLS-1$
            TRACE.error (msg);
            throw new InvalidCredentialsException (msg, e);
        }

        // add bootstrap servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StringUtils.join(messageHubCreds.getKafkaBrokersSasl(), ",")); //$NON-NLS-1$

        // add SASL JAAS property
        String value = JaasUtil.getSaslJaasPropertyValue(messageHubCreds.getUser(), messageHubCreds.getPassword());
        properties.put(JaasUtil.SASL_JAAS_PROPERTY, value);

        // for debugging purpose, trace the messagehub username
        TRACE.debug ("Event Streams instance user = " + messageHubCreds.getUser());
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
        TRACE.info ("Properties from Event Streams credentials: " + logProps); //$NON-NLS-1$
        return properties;
    }

    /**
     * reduces the credentials to first and last two non-whitespace characters and length information.
     * 
     * @param credentials The credentials as raw string
     * @return null if input is null, an empty String, whitespace information or reduced credentials
     */
    private static String prepareCredsForLogging (final String credentials) {
        if (credentials == null) return null;
        int n = credentials.length();
        if (n == 0) return "";
        final String trimmedCreds = credentials.trim();
        int nt = trimmedCreds.length();
        if (nt == 0) return "(" + n + " whitespace characters)";
        if (nt < 5) return credentials;
        StringBuilder result = new StringBuilder(trimmedCreds.substring (0, 2));
        result.append ("...")
        .append (trimmedCreds.substring (nt -2))
        .append (" (").append (n).append (" characters)");
        return result.toString();
    }
}
