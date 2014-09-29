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

package com.sonrisa.swarm.admin.service.exception;

import com.sonrisa.swarm.admin.service.impl.StatusServiceImpl;

/**
 * Exception thrown from {@link StatusServiceImpl} when it receives
 * an invalid request. 
 * 
 * @author Barnabas
 */
@SuppressWarnings("serial")
public class InvalidStatusRequestException extends Exception {

    /**
     * Initializes by setting the reason why the request is invalid
     * @param message
     */
    public InvalidStatusRequestException(String message) {
        super(message);
    }
}
