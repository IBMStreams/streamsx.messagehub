package com.ibm.streamsx.messagehub.credentials;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MessageHubCredentials implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageHubCredentials() {
    }

    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("kafka_rest_url")
    private String kafkaRestUrl;

    @SerializedName("user")
    private String user;

    @SerializedName("password")
    private String password;

    @SerializedName("kafka_brokers_sasl")
    private String[] kafkaBrokersSasl;


    /**
     * @return the apiKey (JSON: 'api_key')
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the kafkaRestUrl (JSON: 'kafka_rest_url')
     */
    public String getKafkaRestUrl() {
        return kafkaRestUrl;
    }
    /**
     * @return the user (JSON: 'user')
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password (JSON: 'password')
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the Kafka brokers (JSON: 'kafka_brokers_sasl')
     */
    public String[] getKafkaBrokersSasl() {
        return kafkaBrokersSasl;
    }

    /**
     * Validates the object for all members being not null.
     * @throws InvalidCredentialsException
     */
    public void validate() throws InvalidCredentialsException {
        if (kafkaRestUrl == null) throw new InvalidCredentialsException ("'kafka_rest_url' could not be parsed from JSON.");
        if (apiKey == null) throw new InvalidCredentialsException ("'api_key' could not be parsed from JSON.");
        if (user == null) throw new InvalidCredentialsException ("'user' could not be parsed from JSON.");
        if (password == null) throw new InvalidCredentialsException ("'password' could not be parsed from JSON.");
        if (kafkaBrokersSasl == null) throw new InvalidCredentialsException ("'kafka_brokers_sasl' could not be parsed from JSON.");
        if (kafkaBrokersSasl.length == 0) throw new InvalidCredentialsException ("'kafka_brokers_sasl' has been parsed as empty list from JSON");
    }
}
