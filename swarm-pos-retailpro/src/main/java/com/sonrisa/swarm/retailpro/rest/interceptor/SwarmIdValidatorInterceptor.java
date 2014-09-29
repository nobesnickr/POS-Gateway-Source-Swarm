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
package com.sonrisa.swarm.retailpro.rest.interceptor;

import com.sonrisa.swarm.retailpro.rest.RetailProApiConstants;
import com.sonrisa.swarm.common.rest.controller.BaseSwarmController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This MCV interceptor checks whether the HTTP request contains the SwarmId in its header.
 *
 * @author joe
 */
public class SwarmIdValidatorInterceptor extends HandlerInterceptorAdapter { 
    
     private static final Logger LOGGER = LoggerFactory.getLogger(SwarmIdValidatorInterceptor.class);
    
    /**
     * Checks whether the HTTP request contains the SwarmId in its header. 
     * 
     * If there is no SwarmId the processing will be terminated and 401 HTTP status will be returned to the client.
     * 
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception 
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        
        boolean result = true;
        
        if (handler instanceof HandlerMethod && ((HandlerMethod)handler).getBean() instanceof BaseSwarmController) {           
            final HandlerMethod methodHandler = (HandlerMethod)handler;
            final BaseSwarmController controller = (BaseSwarmController)methodHandler.getBean();
            if (controller.needsSwarmId() &&  !isSwarmIdOk(request.getHeader(RetailProApiConstants.SWARM_ID))){
                result = false;
                // unauthorized 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        
        return result;
    }
    
    private boolean isSwarmIdOk(final String swarmId) {
        // TODO need to check whether it is an existing swarmID
        return StringUtils.hasLength(swarmId);
    }
    
}
