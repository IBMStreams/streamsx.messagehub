/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
