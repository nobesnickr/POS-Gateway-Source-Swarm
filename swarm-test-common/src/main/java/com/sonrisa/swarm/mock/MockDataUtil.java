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
package com.sonrisa.swarm.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;

/**
 * Utility class for accessing Mock data
 */
public class MockDataUtil { 

    /**
     * Reads resource file
     * @param file
     * @return
     */
    public static InputStream getResourceAsStream(final String file){
        return MockTestData.class.getResourceAsStream(file);
    }
    
    /**
     * Read mock JSON content from resources as String
     * @param resourcePath Filename from MockTestData constants
     * @return JSON file content
     */
    public static String getResourceAsString(String resourcePath){
        try {
            InputStream stream = getResourceAsStream(resourcePath);
            if(stream == null){
                throw new RuntimeException(resourcePath + " not found.");
            }
            return IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            throw new IllegalArgumentException("Filename: " + resourcePath, e);
        }
    }
    
    /**
     * Helper function which reads a resource as json
     * to JsonNode 
     * @param resourcePath Path of the resource
     * @return JsonNode of the response's 
     */
    public static Map<String, Object> getResourceAsMap(String resourcePath){
        return toMap(getResourceAsString(resourcePath));
    }
    
    /**
     * Helper function which reads a resource as json
     * to JsonNode 
     * @param resourcePath Path of the resource
     * @return JsonNode of the response's 
     */
    public static JsonNode getResourceAsJson(String resourcePath){
        return toJson(getResourceAsString(resourcePath));
    }
    
    /**
     * Helper function which reads a resource as json
     * to JsonNode and returns it as ExternalJsonDTO
     * @param resourcePath Path of the resource
     * @return JsonNode of the response's 
     */
    public static ExternalJsonDTO getResourceAsExternalJson(String resourcePath){
        return new ExternalJsonDTO(getResourceAsJson(resourcePath));
    }
    
    /**
     * Helper function which reads a resource as json
     * to JsonNode and returns it as ExternalJsonDTO
     * @param resourcePath Path of the resource
     * @return JsonNode of the response's 
     */
    public static ExternalResponse getResourceAsExternalResponse(String resourcePath){
        return new ExternalResponse(getResourceAsExternalJson(resourcePath));
    }
    
    /**
     * Helper function which reads a resource as json
     * to JsonNode and returns it as ExternalJsonDTO
     * @param resourcePath Path of the resource
     * @return JsonNode of the response's 
     */
    public static ExternalResponse getResourceAsExternalResponse(String resourcePath, Map<String,String> headers){
        return new ExternalResponse(getResourceAsExternalJson(resourcePath), headers);
    }
    
    /**
     * Helper function which converts text to JSON
     * to JsonNode 
     * @param responseText Response by the server
     * @return JsonNode of the response's 
     */
    public static JsonNode toJson(String responseText){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseText, JsonNode.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
    }
    
    /**
     * Helper function which converts text to map
     */
    public static Map<String, Object> toMap(String responseText){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (Map<String, Object>)mapper.readValue(responseText, Map.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
    }
}
