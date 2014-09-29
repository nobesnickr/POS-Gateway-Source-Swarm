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
package com.sonrisa.swarm.posintegration.api.util.impl;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.api.util.JSONFieldReader;

/**
 * Implementation which translates values based on a JSON resource file
 */
public class SimpleJSONFieldTranslator implements JSONFieldReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJSONFieldTranslator.class);
    
    /**
     * Path to JSON file containing the translation values
     */
    private String dictionaryPath;
    
    /**
     * Label
     */
    private String label;
    
    public SimpleJSONFieldTranslator(String dictionaryPath, String label) {
        super();
        this.dictionaryPath = dictionaryPath;
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMeta(String raw) {
        StringBuilder message = new StringBuilder();
        message.append(label).append(": ");
        
        String value = raw;
        
        // Read resource file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionaryPath);
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // Parse it as JSON
            JsonNode json = mapper.readValue(inputStream, JsonNode.class);
            
            // Add either translated or leave the raw value as is
            if(json.has(raw)){
                value = json.get(raw).asText();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to parse JSON dicitonary at {}", dictionaryPath, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.warn("Failed to parse JSON dicitonary at {}", dictionaryPath, e);
            }
        }
        
        // Append the value and return
        return message.append(value).toString();
    }
}
