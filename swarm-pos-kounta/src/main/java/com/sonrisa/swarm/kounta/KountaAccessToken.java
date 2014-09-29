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

package com.sonrisa.swarm.kounta;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuth 2.0 access token for Kounta
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KountaAccessToken {

    /**
     * Access token to authenticate
     */
    @JsonProperty("access_token")
    private String accessToken = "";
    
    /**
     * Token type, addded to the header, e.g. "Bearer"
     */
    @JsonProperty("token_type")
    private String tokenType = "";
    
    /**
     * Scope
     */
    @JsonProperty("scope")
    private String scope;

    public String getToken() {
        return accessToken;
    }
    
    /**
     * Returns the access token so it can be added to the HTTP 
     * request as an <code>Authorization</code> line.
     */
    public String getAuthorizationString(){
        
        StringBuilder authorization = new StringBuilder();
        if(StringUtils.isEmpty(tokenType)){
            authorization.append("Bearer ");
        } else {
            authorization.append(tokenType).append(' ');
        }
        
        return authorization.append(accessToken).toString();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
