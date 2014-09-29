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

package com.sonrisa.swarm.posintegration.api;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.UriUtils;

import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.util.RestUrlBuilder;

/**
 * Model class for a REST command. A REST command has an URI, has and account for authentication and has parameters.
 */
public class ExternalCommand<T extends SwarmStore> {

    /** Account to authenticate the command */
    private T account;

    /** Request URI, e.g. <code>Invoices</code> */
    private String uri;

    /** Request parameters, optional */
    private Map<String, String> params;
    
    /** Request configuration parameters passed to the API for any reason */
    private Map<String, String> config;

    /**
     * Create new command which can be executed by {@link ExternalAPI}
     * 
     * @param account Account to authenticate the command
     * @param uri Request URI, e.g. <code>Invoices</code>
     * @param params Parameters, e.g. REST GET or POST keys
     */
    public ExternalCommand(T account, String uri, Map<String, String> params, Map<String,String> config) {
        this.account = account;
        this.uri = uri;
        this.params = Collections.unmodifiableMap(params);
        this.config = Collections.unmodifiableMap(config);
    }
    
    /**
     * Create new command which can be executed by {@link ExternalAPI}
     * 
     * @param account Account to authenticate the command
     * @param request Request URI, e.g. <code>Invoices</code>
     * @param params Parameters, e.g. REST GET or POST keys
     */
    public ExternalCommand(T account, String request, Map<String, String> params) {
        this(account, request, params, new HashMap<String,String>());        
    }

    /**
     * Create new command which can be executed by {@link ExternalAPI}
     * 
     * @param account Account to authenticate the command
     * @param request Request URI, e.g. <code>Invoices</code>
     */
    public ExternalCommand(T account, String request) {
        this(account, request, new HashMap<String, String>());
    }

    /**
     * Clone the command with different parameters

     * @param params Additional parameters, e.g. REST GET or POST keys
     * @return Command with the parameters specified (ignoring original parameters)
     */
    public ExternalCommand<T> withParams(Map<String, String> newParams) {
        return new ExternalCommand<T>(account, uri, newParams);
    }
    
    /**
     * Clone the command with different configuration parameters

     * @param params Additional parameters, e.g. HTTP headers
     * @return Command with the connfig specified (ignoring original parameters)
     */
    public ExternalCommand<T> withConfig(Map<String, String> newConfig) {
        return new ExternalCommand<T>(account, uri, params, newConfig);
    }
    
    /**
     * Converts the URI and the parameters into a URI query, e.g.
     * <code>/API/Invoices?order_by=id&take=100&skip=300</code>
     * 
     * @return
     */
    public String getUrlQueryString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.uri);
        if(!params.isEmpty()){
            sb.append('?');
            sb.append(RestUrlBuilder.prepareGetFields(params));
        }
        
        try {
            return UriUtils.encodeFragment(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get configuration information for debugging purposes
     * @return
     */
    public String getConfigurationInfo(){
        return config.toString();
    }

    public T getAccount() {
        return account;
    }

    public String getURI() {
        return uri;
    }

    public Map<String, String> getParams() {
        return params;
    }
    
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "ExternalCommand [accountClass=" + account.getClass().getSimpleName() + ", getoUrlQueryString()=" + getUrlQueryString() + "]";
    }
}
