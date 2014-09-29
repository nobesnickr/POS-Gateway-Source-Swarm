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
package com.sonrisa.swarm.legacy.model;

/**
 * This class encapsulates the result of an entity validation.
 * 
 * It can be used for example during the moving of the staging entities
 * at the validation of the entities.
 *
 * @author joe
 */
public class EntityValidationResult {
    
    /** Result of the validation. True indicates the entity is OK and can be processed, 
     * false means there is one or more invalid values in the entity. */
    private boolean success;
    /** Human readable description of the validation error. */
    private String message;
    
    /**
     * Method to instantiate a result object if the validation has been succeeded.
     * 
     * @return 
     */
    public static EntityValidationResult success(){
        return new EntityValidationResult(true, "no validation error");
    }
    
    /**
     * Method to instantiate a result object if the validation has been failed.
     * 
     * @param msg
     * @return 
     */
    public static EntityValidationResult failure(String msg){
        return new EntityValidationResult(false, msg);
    }
    
    /**
     * Private constructor.
     * 
     * @param result
     * @param message 
     */
    private EntityValidationResult(boolean result, String message) {
        this.success = result;
        this.message = message;
    }

    
    public boolean isSuccess() {
        return success;
    }   

    public String getMessage() {
        return message;
    }
      
}
