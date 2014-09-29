package com.sonrisa.swarm.posintegration.exception;

/**
 * Exception when External API denies credentials.
 */
public class ExternalApiBadCredentialsException extends ExternalApiException {
    private static final long serialVersionUID = -3259502188096988632L;

    public ExternalApiBadCredentialsException(String errorMsg){
        super(errorMsg);
    }
}
