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
package com.sonrisa.swarm.retailpro.dao.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * Abstract base class for {@link RpDynamicMappingDaoImpl} and {@link rpLogConfigurationDaoImpl},
 * responsible for accessing JSON files based on swarmIds
 */
public abstract class BaseJsonFileDao {
    
    /**
     * Location where the default JSON file is located, this file should be in the resource directory
     */
    public final String defaultJsonResourcePath;
    
    /**
     * Initializes a new instance by setting the custom folder and the default path
     * @param customJsonFolder Location where the swarmId specific Json files are located
     * @param defaultResourcePath Location where the default JSON file is located
     */
    protected BaseJsonFileDao(final String defaultResourcePath){
        this.defaultJsonResourcePath = defaultResourcePath;
    }
    
    /**
     * Attempts to read custom JSON for swarmId, if this attempt fails then returns 
     * with the default mapping. 
     * 
     * @param path Identifier of the swarm partner
     * @return String of a valid JSON
     */
    protected JsonNode getCustomOrDefaultJson(String path){
        JsonNode mapping = getJsonFromCustomFolder(path);
       
        if(mapping != null){
            return mapping;
        } else {
            return getDefaultJson();
        }
    }
    
    /**
     * Attempts to read a file located in the custom folder and parse it as JSON
     * @param fileName 
     * @return JsonNode or null if parsing failed or file wasn't found
     */
    protected JsonNode getJsonFromCustomFolder(String fileName){
        JsonNode json = null;
        if(getCustomJsonFolder() != null){
            String customJsonFile = getCustomJsonFolder() + fileName.replaceAll("[/\\\\\\~]", "") + ".json";
            logger().debug("Trying to read custom JSON from {} ", customJsonFile);
            
            try {
                json = getJsonFromInputStream(customJsonFile, new FileInputStream(customJsonFile));
                logger().debug("Custom JSON has been found for {}", customJsonFile);
            } catch (FileNotFoundException e) {
                logger().debug("Custom JSON not found for {}", customJsonFile, e);
                json = null;
            }
        } else {
            logger().debug("No custom folder provided");
        }
        
        return json;
    }
    
    /**
     * Retrieves JSON from resource folder
     * @param path
     * @return The JSON or empty JSON if parsing fails
     */
    protected JsonNode getJsonFromResource(String path){
        JsonNode mapping = null;
        InputStream inputStream =  getClass().getClassLoader().getResourceAsStream(path);
        if(inputStream != null){
            mapping = getJsonFromInputStream("DEFAULT", inputStream);
        }
    
        if(mapping == null){
            logger().error("Failed to load default json file {}", path);
            mapping = parseJson("{}");
        }
        return mapping;
    }
    
    /**
     * Read the JSON node set as default response for this DAO
     * @return The default JSON or empty JSON if unparseable
     */
    protected JsonNode getDefaultJson(){
        return getJsonFromResource(defaultJsonResourcePath);
    }
    
    /**
     * Attempts to read a JSON file from an InputStream 
     * 
     * @param idForLog Name of the file which is attempted to be read, this value is only used for logging
     * @param stream Input stream to read JSON from, normally either file in userdir or resource file
     * @return String of a valid JSON
     */
    private JsonNode getJsonFromInputStream(String idForLog, InputStream stream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            
            StringBuilder stringBuilder = new StringBuilder();
            
            // Read each line
            String line = null;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            return parseJson(stringBuilder.toString());
        } catch (IOException e) {
            logger().warn("An error occured while trying to read JSON from {}{}", idForLog, e);
        } 

        return null;
    }
    
    /**
     * Attempts to parse text as JSON, if fails returns null
     * @param text Text to parsed
     * @return JsonNode or null
     */
    private JsonNode parseJson(String text){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(text, JsonNode.class);
        } catch (JsonParseException e) {
            logger().warn("Provided custom JSON is invalid, default JSON will be sent instead", e);
        } catch (JsonMappingException e) {
            logger().warn("Provided custom JSON is invalid, default JSON will be sent instead", e);
        } catch (IOException e) {
            logger().warn("Provided custom JSON is invalid, default JSON will be sent instead", e);
        }
        return null;
    }
    
    /**
     * Get the logger of the extending class
     * @return Logger to be used for logging
     */
    protected abstract Logger logger();

    public abstract String getCustomJsonFolder();
}
