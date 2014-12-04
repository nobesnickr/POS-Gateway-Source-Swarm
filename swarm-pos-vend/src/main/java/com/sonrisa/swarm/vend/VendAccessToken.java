package com.sonrisa.swarm.vend;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuth 2.0 access token for Vend
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendAccessToken {

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
