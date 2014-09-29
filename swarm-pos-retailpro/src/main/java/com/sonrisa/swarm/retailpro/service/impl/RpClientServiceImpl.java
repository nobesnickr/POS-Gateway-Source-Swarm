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
package com.sonrisa.swarm.retailpro.service.impl;

import com.sonrisa.swarm.retailpro.dao.impl.RpClientDao;
import com.sonrisa.swarm.retailpro.dao.impl.RpPluginDao;
import com.sonrisa.swarm.retailpro.model.RpClientEntity;
import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import com.sonrisa.swarm.retailpro.rest.model.RpClientJson;
import com.sonrisa.swarm.retailpro.rest.model.RpHeartbeatJson;
import com.sonrisa.swarm.retailpro.service.RpClientService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityMapper;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link RpClientService} interface.
 *
 * @author joe
 */
@Service
@Transactional
public class RpClientServiceImpl implements RpClientService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpClientServiceImpl.class);
    
    @Autowired
    private RpClientDao clientDao;
 
    @Autowired
    private RpPluginDao pluginDao;
    
    @Autowired
    private EntityMapper mapper;

    /**
     * 
     * DEPRECATED: invocation of this method occurs 500 server error.
     * Instead of saving the client version to the DB, we just log it to the client log file.
     * 
     * {@inheritDoc }
     * 
     * @param swarmId
     * @param rpClientJsons
     * @return 
     */
    @Override
    @Deprecated
    public void save(String swarmId, Collection<RpClientJson> rpClientJsons) {
        if (rpClientJsons != null) {

            for (RpClientJson clientJson : rpClientJsons) {

                LOGGER.debug("Saving RetailPro client object: " + clientJson + " swarmId: " + swarmId);
                final String componentId = clientJson.getComponentId();

                RpClientEntity rpClient = clientDao.findBySwarmIdAndComponentId(swarmId, componentId);

                if (rpClient == null) {
                    LOGGER.debug("RetailPro client component does not exist yet, a new one will be created: " + clientJson + " swarmId: " + swarmId);
                    rpClient = new RpClientEntity(swarmId, componentId);
                } else {
                    LOGGER.debug("RetailPro client already exists, it will be updated: " + clientJson + " swarmId: " + swarmId);
                }

                mapper.copyJsonToEntity(clientJson, rpClient);
                clientDao.persist(rpClient);
                clientDao.flush();
                LOGGER.debug("RetailPro client has been saved, ID: " + rpClient.getId());
            }

        }
    }

    /**
     * {@inheritDoc }
     * 
     * @param id
     * @return 
     */
    @Override
    public RpClientEntity find(Long id) {
        return clientDao.findById(id);
    }

    /**
     * {@inheritDoc }
     * 
     * @param swarmId
     * @param json 
     */
    @Override
    public void heartbeat(final String swarmId, final RpHeartbeatJson json) {
        RpPluginEntity plugin = pluginDao.findBySwarmId(swarmId);
        
        if (plugin == null){      
            plugin = new RpPluginEntity(swarmId);
            LOGGER.debug("RetailPro plugin does not exist yet, a new one will be created: " + json + " swarmId: " + swarmId);
        }
        // the plugin exists, we can update its heartbeat timestamp and its version
        plugin.setHeartbeat(new Date());
        plugin.setPluginVersion(json.getRpPluginVersion());
        
        if (LOGGER.isDebugEnabled()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOGGER.debug("Plugin's (SwarmId: "+swarmId+") heartbeat has been updated: " 
                    + sdf.format(plugin.getHeartbeat()) + " version: " + plugin.getPluginVersion());
        }
        pluginDao.persist(plugin);
        pluginDao.flush();
    }            
}
