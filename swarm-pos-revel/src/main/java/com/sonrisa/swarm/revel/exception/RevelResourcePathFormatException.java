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
package com.sonrisa.swarm.revel.exception;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;


/**
 * Exception thrown when a Revel field is expected to be a resource path(e.g. /resources/PosStation/2/), but
 * doesn't match the formatting criteria
 */
public class RevelResourcePathFormatException extends ExternalExtractorException {
    
    public RevelResourcePathFormatException(String resourcePath, String expectedPrefix){
        super("Revel resource path was expected to start with " + expectedPrefix + " but was " + resourcePath);
    }
    
    public RevelResourcePathFormatException(String message){
        super(message);
    }
    
    public RevelResourcePathFormatException(Exception e){
        super(e);
    }
}