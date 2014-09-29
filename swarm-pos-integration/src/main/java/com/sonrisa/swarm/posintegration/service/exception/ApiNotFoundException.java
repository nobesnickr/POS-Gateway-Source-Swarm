/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.posintegration.service.exception;

import com.sonrisa.swarm.posintegration.service.ApiService;

/**
 * Exception thrown from {@link ApiService} when called with an API it doesn't find
 * @author Barnabas
 *
 */
public class ApiNotFoundException extends RuntimeException {
    
    /**
     * Throw when API with given <i>apiName</i> is not found
     * @param apiName
     */
    public ApiNotFoundException(String apiName){
        super("API not found: " + apiName);
    }
    
    /**
     * Throw when API with given <i>apiId</i> is not found
     * @param apiName
     */
    public ApiNotFoundException(Long apiId){
        super("API not found for id: " + apiId);
    }
}
