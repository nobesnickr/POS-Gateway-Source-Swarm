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
package com.sonrisa.shopify.model;

import org.springframework.util.StringUtils;

/**
 * This class encapsulates the result of the temp token exchange request.
 *
 * @author joe
 */
public class TokenExchangeResult {
    
    private String permToken;
    
    private String errorMsg;
    private String httpCode;
    private String responseBody;
    
    // ------------------------------------------------------------------------
    // ~ Static init methods
    // ------------------------------------------------------------------------

    public static TokenExchangeResult success(final String permToken) {
        final TokenExchangeResult result = new TokenExchangeResult();
        result.setPermToken(permToken); 
        return result;
    }
    
    public static TokenExchangeResult error(final String errorMsg, String httpCode, String responseBody) {
        final TokenExchangeResult result = new TokenExchangeResult();
        result.setErrorMsg(errorMsg);
        result.setHttpCode(httpCode);
        result.setResponseBody(responseBody);
        return result;
    }
    
    public static TokenExchangeResult error(final String errorMsg) {
        return TokenExchangeResult.error(errorMsg, null, null);
    }
    
    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------
    
    /**
     * Returns whether the token exchange proccess has been succeeded.
     * 
     * @return 
     */
    public boolean succeeded(){
        return !StringUtils.hasLength(errorMsg);
    }

    @Override
    public String toString() {
        return "TokenExchangeResult{errorMsg=" 
                + errorMsg + ", httpCode=" + httpCode + ", responseBody=" + responseBody + '}';
    }
    
    
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------
    
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getPermToken() {
        return permToken;
    }

    public void setPermToken(String permToken) {
        this.permToken = permToken;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    
    
    
}
