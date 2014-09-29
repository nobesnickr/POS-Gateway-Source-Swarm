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
package com.sonrisa.swarm.rest.interceptor;

import com.sonrisa.swarm.common.rest.controller.BaseSwarmController;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

    
/**
 * Logger interceptor, it is responsible for logging the incoming HTTP request.
 *
 * @author joe
 */
public class LoggingInterceptor extends HandlerInterceptorAdapter  {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    /**
     * Logging before the MVC controller handles the request.
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
                        
        if (LOGGER.isDebugEnabled()) {                 
            if (handler instanceof HandlerMethod && ((HandlerMethod)handler).getBean() instanceof BaseSwarmController){
                final HandlerMethod methodHandler = (HandlerMethod)handler;
                final BaseSwarmController controller = (BaseSwarmController)methodHandler.getBean();
                final String controllerClassName = controller.getClass().getSimpleName();
                
                controller.logger().debug(controllerClassName + " has been invoked. " + requestInfo(request));
            }
        }
         
        // returns true in order to continue the request handling
        return true;
    }
    
    /**
     * Concatenates the basic information about the HTTP request,.
     * E.g.: 
     * 
     * @param request
     * @return 
     */
    private String requestInfo(HttpServletRequest request){
        StringBuilder strBuilder = new StringBuilder();
        
        strBuilder.append("Request URI: ");
        strBuilder.append(request.getRequestURI());
        strBuilder.append("; ");
        
        strBuilder.append("Request method: ");
        strBuilder.append(request.getMethod());
        strBuilder.append("; ");
        
        appendHeaders(request, strBuilder);
        appendParams(request, strBuilder);
               
        return strBuilder.toString();                      
    }
    
    /**
     * Appends the headers from the request to the given builder.
     * 
     * @param request
     * @param strBuilder 
     */
    private void appendHeaders(HttpServletRequest request, StringBuilder strBuilder) {
        Enumeration headers = request.getHeaderNames();
        strBuilder.append("Request headers: [");
        while(headers.hasMoreElements()){
            Object key = headers.nextElement();
            if (key instanceof String){
                strBuilder.append(key);
                strBuilder.append(":");
                strBuilder.append(request.getHeader((String)key));
                strBuilder.append("; ");   
            }
        }
        strBuilder.append("] ");
    }

    /**
     * Appends the parameters from the request to the given builder.
     * 
     * @param request
     * @param strBuilder 
     */
    private void appendParams(HttpServletRequest request, StringBuilder strBuilder) {
        Map params = request.getParameterMap();
        strBuilder.append("Request params: [");
        for (Object entry : params.entrySet()){
            if (entry instanceof String){
                strBuilder.append(entry);
                strBuilder.append(":");
                strBuilder.append(request.getParameter((String)entry));
                strBuilder.append("; ");   
            }    
        }
        strBuilder.append("] ");
    }
    
    
}

