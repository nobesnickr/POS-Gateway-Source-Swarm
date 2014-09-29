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
package com.sonrisa.swarm.posintegration.extractor.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * Utility class which helps to build REST request URIs
 * @author sonrisa
 *
 */
public class RestUrlBuilder {
    
    /**
     * Given the get fields, creates url query
     * @retval E.g. a=1&b=2&c=3
     */
    public static String prepareGetFields(Map<String,String> params) {
  
        StringBuilder retval = new StringBuilder();
        for(Map.Entry<String, String> item : params.entrySet()){
            if(retval.length() != 0){
                retval.append("&");
            }
            retval.append(item.getKey() + "=" + item.getValue());
        }
        return retval.toString();
    }
    

    
    /**
     * Given the parameters parameter passed to the httpPost method prepare the post fields
     */
    public static HttpEntity preparePostFields(Map<String,String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
  
        for(Map.Entry<String, String> item : params.entrySet()){
            list.add(new BasicNameValuePair(item.getKey(), item.getValue()));
        }
        
        // Encode post fields
        try {
            return new UrlEncodedFormEntity(list);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
