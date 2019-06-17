/**
 * 
 */
package com.ibm.streamsx.messagehub.credentials;

import com.ibm.streamsx.kafka.KafkaOperatorRuntimeException;

/**
 * This exception indicates invalid service credentials,
 * i.e. when the data given as credentials cannot be used for connecting and authenticating.
 * This exception is not thrown on authentication failures.
 * 
 * @author The IBM toolkit team
 * @since toolkit version 2.0
 */
public class InvalidCredentialsException extends KafkaOperatorRuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidCredentialsException.
     */
    public InvalidCredentialsException() {
    }

    /**
     * Creates a new InvalidCredentialsException.
     * @param message the exception message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidCredentialsException.
     * @param cause the cause of the exception
     */
    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new InvalidCredentialsException.
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new InvalidCredentialsException.
     * @param message the exception message
     * @param cause the cause of the exception
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidCredentialsException (String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
