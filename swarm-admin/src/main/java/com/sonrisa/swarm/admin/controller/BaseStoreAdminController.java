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
package com.sonrisa.swarm.admin.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.admin.model.error.ErrorEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidAdminRequestException;
import com.sonrisa.swarm.admin.service.exception.UnknownStoreException;

/**
 * Base class for controllers updating stores
 * @author Barnabas
 *
 */
public abstract class BaseStoreAdminController extends BaseAdminController {

    /**
     * Handle exception for invalid request
     */
    @ExceptionHandler(InvalidAdminRequestException.class)
    public @ResponseBody ResponseEntity<ErrorEntity> handleStatusServiceException(HttpServletRequest request, InvalidAdminRequestException exception){
        logger().warn("Failed to serve invalid request: " + request.getRequestURI(), exception);
        
        if(exception instanceof UnknownStoreException){
            return new ResponseEntity<ErrorEntity>(new ErrorEntity("Not found", exception.getMessage()), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<ErrorEntity>(new ErrorEntity("Invalid request", exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
