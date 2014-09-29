package com.sonrisa.swarm.posintegration.api.service.exception;

/**
 * Exception thrown when scanning for locations fails.
 */
public class StoreScanningException extends Exception {
    private static final long serialVersionUID = -4503146771479871795L;

    public StoreScanningException(String message){
        super(message);
    }
    
    public StoreScanningException(String message, Throwable throwable){
        super(message, throwable);
    }
    
    /**
     * Get user friendly error message to be printed out to the user
     * @return
     */
    public String getUserFriendlyErrorMessage(){
        return getMessage();
    }
}

