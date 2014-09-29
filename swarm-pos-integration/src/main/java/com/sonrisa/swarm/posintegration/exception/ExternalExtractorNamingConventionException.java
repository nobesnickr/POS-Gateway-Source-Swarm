/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.posintegration.exception;

/**
 * Exception raised when a certain class expected to be found due
 * to the POS naming convention is not found.
 * 
 * E.g. ErplyInvoiceDTO is not found for InvoiceDTO when executing Erply
 * @author sonrisa
 *
 */
@SuppressWarnings("serial")
public class ExternalExtractorNamingConventionException extends RuntimeException {

    public ExternalExtractorNamingConventionException(){
        super();
    }
    
    public ExternalExtractorNamingConventionException(String message){
        super(message);
    }
    
    public ExternalExtractorNamingConventionException(Exception e){
        super(e);
    }
}
