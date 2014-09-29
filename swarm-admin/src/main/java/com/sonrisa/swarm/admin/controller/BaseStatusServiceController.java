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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.admin.model.error.ErrorEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.OrderDirection;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;

/**
 * Base class for status service controllers
 */
public abstract class BaseStatusServiceController extends BaseAdminController {
    
    /**
     * JSON's data key
     */
    public static final String DATA_KEY = "stores";
        
    /**
     * Handle exception for invalid request
     */
    @ExceptionHandler(InvalidStatusRequestException.class)
    public @ResponseBody ResponseEntity<ErrorEntity> handleStatusServiceException(HttpServletRequest request, InvalidStatusRequestException exception){
        logger().warn("Failed to serve invalid request: " + request.getRequestURI(), exception);
        return new ResponseEntity<ErrorEntity>(new ErrorEntity("Invalid request", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Converts comma-separated string to a set
     * @throws InvalidStatusRequestException
     */
    protected Set<String> requestParamsAsSet(String value) throws InvalidStatusRequestException {
        Set<String> retVal = new HashSet<String>();
        
        // Missing request parameter is empty set
        if(StringUtils.isEmpty(value)){
            return retVal;
        }
        
        for(String field : value.split(",")){
            if(retVal.contains(field)){
                throw new InvalidStatusRequestException("Duplicate value: " + field);
            } else {
                // Alphanumeric or underscore
                if(field.matches("^[A-Za-z0-9_]+$")){
                    retVal.add(field);
                } else {
                    throw new InvalidStatusRequestException("Illegal characters in: <" + field + ">");
                }
            }
        }
        
        return retVal;
    }
    
    /**
     * Parse string for order direction
     * @param orderDir
     * @return
     * @throws InvalidStatusRequestException
     */
    protected OrderDirection requestParamAsOrderDir(String orderDir)  throws InvalidStatusRequestException {
        
        for(OrderDirection candidate : OrderDirection.values()){
            if(candidate.getValue().equals(orderDir)){
                return candidate;
            }
        }
        
        logger().warn("Value can't be translated to order direction: {}", orderDir);
        throw new InvalidStatusRequestException("Illegal value for order_dir: " + orderDir + " allowed values are " + StringUtils.join(OrderDirection.values(), ","));
    }
    
    /**
     * Parse comma-separated string as list of store status
     * @param value
     * @return
     */
    protected Set<StoreStatus> requestParamsAsStatusSet(String value) throws InvalidStatusRequestException {
        Set<String> stringValues = requestParamsAsSet(value);
        
        Set<StoreStatus> retVal = new HashSet<StoreStatus>();
        for(String field : stringValues){
            try {
                StoreStatus enumValue = StoreStatus.valueOf(field);
                retVal.add(enumValue);
            } catch (IllegalArgumentException e){
                logger().warn("Value can't be translated to status: {}", field, e);
                throw new InvalidStatusRequestException("Illegal value for status: " + field + " allowed values are " + StringUtils.join(StoreStatus.values(), ","));
            }
        }
        
        return retVal;
    }
}
