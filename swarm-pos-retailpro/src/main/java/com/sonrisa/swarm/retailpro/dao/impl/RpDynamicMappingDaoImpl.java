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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.retailpro.dao.RpDynamicMappingDao;

/**
 * Implementation of the {@link RpDynamicMappingDao} interface
 */
@Repository
public class RpDynamicMappingDaoImpl extends BaseJsonFileDao implements RpDynamicMappingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpDynamicMappingDaoImpl.class);
    
    /**
     * Location where the default json file can be found, this is a resource file
     */
    private static final String DEFAULT_RESOURCE_PATH_PREFIX = "rpDynamicMapping/map.";
    
    /**
     * Location where the swarm id specific json files can be found, this is a folder
     */
    @Value("${user.home}/swarm/rpMappings/")
    private String customMappingFolder;   
        
    /**
     * Initialize DAO by setting the default json file and the folder of the custom JSONs
     */
    public RpDynamicMappingDaoImpl(){
        super(DEFAULT_RESOURCE_PATH_PREFIX + "default.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicMapping(final String requestSwarmId, final String requestPosSoftware){
        
        String swarmId = !StringUtils.isEmpty(requestSwarmId) ? requestSwarmId : "";
        String posSoftware = !StringUtils.isEmpty(requestPosSoftware) ? requestPosSoftware : "default";
        
        JsonNode mapping = super.getJsonFromCustomFolder(swarmId + "." + posSoftware);

        if(mapping != null){
            // Use custom mapping
            return mapping.toString();
        } else {
            getJsonFromResource(DEFAULT_RESOURCE_PATH_PREFIX + posSoftware + ".json").toString();
            return super.getDefaultJson().toString();    
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }

    public String getCustomMappingFolder() {
        return customMappingFolder;
    }

    public void setCustomMappingFolder(String customMappingFolder) {
        this.customMappingFolder = customMappingFolder;
    }

    @Override
    public String getCustomJsonFolder() {
        return getCustomMappingFolder();
    }
}
