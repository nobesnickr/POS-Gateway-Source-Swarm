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

package com.sonrisa.swarm.lspro.controller.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.lspro.controller.LsProStoreController;

/**
 * Response sent by {@link LsProStoreController} when registration is successful.
 * @author Barnabas
 */
public class LsProRegistrationErrorEntity {

    /**
     * Error message
     */
    private String error;
    
    /**
     * Error message of cause
     */
    @JsonProperty("inner_error")
    private String innerError;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getInnerError() {
        return innerError;
    }

    public void setInnerError(String innerError) {
        this.innerError = innerError;
    }
}
