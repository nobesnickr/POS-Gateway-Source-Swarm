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
package com.sonrisa.swarm.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;

/**
 * Common base class for {@link ExtractorLauncherReader} and {@link InvoiceProcessorReader}
 */
public abstract class BasePartitionedJobReader<T extends BaseSwarmEntity> extends JpaPagingItemReader<T> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePartitionedJobReader.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
       
    /** 
     * Name of the API.
     * 
     * The API defines which stores need to be extracted and 
     * which {@link ExternalExtractor} implementation has to be used
     * for this process.
     */
    private String apiName;  
    
    private Long fromId = null;
    
    private Long toId = null;
    
    private List<Long> idList = null;

    /**
     * This method is responsible for finding the apiId
     * by its name and for setting it as a parameter of
     * the query execution. 
     * 
     * The query execution will retrieve the stores that 
     * need to be extracted.
     * 
     * @throws Exception 
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Long apiId = findApiId(apiName);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("apiId", apiId);
        
        if(idList != null){
            params.put("idList", idList);
        }
        
        if(fromId != null){
            params.put("fromId", fromId);
        }
        
        if(toId != null){
            params.put("toId", toId);
        }
        
        // setting the apiId as a parameter of query execution
        setParameterValues(params);
    }

    /**
     * This method finds the apiId by its name.
     *
     * @param apiName
     * @return
     */
    private Long findApiId(final String apiName) {
        LOGGER.debug("Finding apiId for this apiName: " + apiName);

        Long apiId;
        try {
            apiId = jdbcTemplate.queryForObject("select api_id from apis where name like ? ", Long.class, apiName);
        } catch (DataAccessException ex) {
            LOGGER.error("An exception occured during the query execution, the apiId can not be determined. ApiName: " + apiName, ex);
            return null;
        }

        if (apiId == null) {
            LOGGER.error("An api with this name can not be found: " + apiName);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ApiId has been found for the " + apiName + " api: " + apiId);
            }
        }

        return apiId;
    }

    /**
     * Sets the apiName that describes which api 
     * will be used for the extraction.
     * 
     * @param apiName 
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
