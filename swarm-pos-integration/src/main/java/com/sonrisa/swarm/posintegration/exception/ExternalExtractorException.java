package com.sonrisa.swarm.posintegration.exception;

import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * External extractors retrieve data from remote systems (Erply, MerchantOS, etc.)
 * and save them into a local datastore (MySQL, NoSQL, etc.). If this process
 * fails during the extraction process, the communication with the remote system 
 * an ExternalExtractorException is generated;
 * @author sonrisa
 *
 */
public class ExternalExtractorException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExternalExtractorException(){
		super();
	}
	
	public ExternalExtractorException(String message){
		super(message);
	}
	
	public ExternalExtractorException(String message, Class<? extends DWTransferable> clazz){
	    super(String.format("Error while extracting %s: %s", clazz.getSimpleName(), message));
	}
	
	public ExternalExtractorException(String message, Throwable cause){
        super(message, cause);
    }
	
	public ExternalExtractorException(Throwable cause){
		super(cause);
	}
	
	/**
	 * Error message which can be printed to the user
	 */
	public String getUserFriendlyError() {
	    return "Error while communicating with external party";
	}
}
