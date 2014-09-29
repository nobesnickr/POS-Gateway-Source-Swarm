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

import org.springframework.util.StringUtils;

/**
 * Helper class to builder REST URIs for Kounta
 */
public class KountaUriBuilder {
    
    /**
     * URI for the REST service for the company's basic information is accessible
     */
    public static final String COMPANY_INFO_URI = "companies/me.json";
    
    private KountaUriBuilder(){
        // This is a utility class
    }

    /**
     * Build site/company specific URI
     */
    public static String getSiteUri(String company, String site, String resource){

        // Build REST URI
        StringBuilder queryUrl = new StringBuilder();
        
        if(StringUtils.hasLength(company)){
            queryUrl.append("companies/").append(company).append('/');
            
            if(StringUtils.hasLength(site)){
                queryUrl.append("sites/").append(site).append('/');
            }
        }
        
        queryUrl.append(resource);
        return queryUrl.toString();
    }
    
    /**
     * Build site/company specific URI
     */
    public static String getSiteUri(KountaAccount account, String resource){
        return getSiteUri(account.getCompany(), account.getSite(), resource);
    }
    
    /**
     * Build company specific URI
     */
    public static String getCompanyUri(String company, String resource){
        return getSiteUri(company, null, resource);
    }

    /**
     * Build company specific URI
     */
    public static String getCompanyUri(KountaAccount account, String resource){
        return getSiteUri(account.getCompany(), null, resource);
    }
}
