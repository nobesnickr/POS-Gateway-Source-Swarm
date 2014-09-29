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

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.retailpro.dao.RpLogConfigurationDao;

/**
 * Implementation of the {@link RpLogConfigurationDaoImpl} interface.
 */
@Repository
public class RpLogConfigurationDaoImpl extends BaseJsonFileDao implements RpLogConfigurationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpDynamicMappingDaoImpl.class);
    
    /**
     * Location where the default json file can be found, this is a resource file
     */
    private static final String DEFAULT_RESOURCE_PATH = "rpDynamicMapping/loglevel.json";
    
    /**
     * Only certain values are accepted for the keys of JSON files, these are the standard .NET log levels
     */
    private static final String[] ALLOWED_LOG_LEVELS = {"Trace", "Debug", "Info", "Warn", "Error", "Fatal", "Off"};
    
    /**
     * Location where the swarm id specific json files can be found
     */
    @Value("${user.home}/swarm/rpLogLevels/")
    private String customLogLevelConfigFolder;   
        
    /**
     * Initialize DAO by setting the default and the custom folder of the JSONs
     */
    public RpLogConfigurationDaoImpl(){
        super(DEFAULT_RESOURCE_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLogLevelConfiguration(String swarmId){
        JsonNode mapping = getJsonFromCustomFolder(swarmId);
        
        if(mapping != null){
            if(areLogLevelsValid(mapping)){
                return mapping.toString();
            } else {
                LOGGER.warn("Custom log configuration for {} contains invalid log level, using default instead", swarmId);
            }
        }
        
        return getDefaultJson().toString();
    }
    
    /**
     * Verifies that only the ALLOWED_LOG_LEVELS are present as values in the json
     * @return
     */
    private boolean areLogLevelsValid(JsonNode inspectedJson){
        
        final List<String> allowedValues = Arrays.asList(ALLOWED_LOG_LEVELS);
        for(JsonNode entry : inspectedJson){
            if(!allowedValues.contains(entry.textValue())){
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }

    public String getCustomLogLevelConfigFolder() {
        return customLogLevelConfigFolder;
    }

    public void setCustomLogLevelConfigFolder(String customLogLevelConfigFolder) {
        this.customLogLevelConfigFolder = customLogLevelConfigFolder;
    }

    @Override
    public String getCustomJsonFolder() {
        return getCustomLogLevelConfigFolder();
    }
}
