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
package com.sonrisa.swarm.retailpro.util.mapper;

import com.sonrisa.swarm.model.BaseSwarmEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ProductStage;

/**
 * Helper class to encapsulate the entities created from the JSON received JSON objects.
 *
 * @author joe
 */
public class EntityHolder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityHolder.class);
    
    private Map<String, List<BaseSwarmEntity>> container;
    
    public void addEntity(BaseSwarmEntity entity) {
        if (entity != null){
            
            // first time the container has to be instantiated
            if (container == null){
                container = new HashMap<String, List<BaseSwarmEntity>>();                    
            }
            String entityClassName = entity.getClass().getSimpleName();
        
            // first time the list of this type has to be instantiated
            if (!container.containsKey(entityClassName)){
                container.put(entityClassName, new ArrayList<BaseSwarmEntity>());
            }
            
            container.get(entityClassName).add(entity);
            LOGGER.debug("New entity has been added, tpye:" + entityClassName + " entity: " + entity);
        }                     
    }
    
    private <T extends BaseSwarmEntity> List<T> getEntitiesByType(Class<T> type){
        List result = Collections.EMPTY_LIST;
        
        String className = type.getSimpleName();
        if (container != null && container.containsKey(className)){
            result = container.get(className);
        }
        
        return (List<T>)result;
    }

    public List<InvoiceStage> getInvoices() {       
        return getEntitiesByType(InvoiceStage.class);
    }

    public List<InvoiceLineStage> getItems() {
        return getEntitiesByType(InvoiceLineStage.class);        
    }

    public List<CustomerStage> getCustomers() {
        return getEntitiesByType(CustomerStage.class);        
    }

    public List<ProductStage> getProducts() {
        return getEntitiesByType(ProductStage.class);
    }        

    /**
     * Returns information about the number of different entites saved
     * @return
     */
    public String getEntityCountInfo(){
        StringBuilder sb = new StringBuilder();
        
        for(Entry<String,List<BaseSwarmEntity>> entry : this.container.entrySet()){
            if(sb.length() != 0){
                sb.append(",");
            }
            sb.append(entry.getKey() + "=" + entry.getValue().size());
        }
        
        return sb.toString();
    }
}
