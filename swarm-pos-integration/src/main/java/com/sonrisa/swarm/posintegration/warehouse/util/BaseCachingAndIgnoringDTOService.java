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
package com.sonrisa.swarm.posintegration.warehouse.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base class for implementations of the SwarmDataStore extending
 * its functionality by saving all item's timeStamp in a cache. 
 */
public abstract class BaseCachingAndIgnoringDTOService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCachingAndIgnoringDTOService.class);
    
    /** 
     * Triggering getCachedLastTimestamp doesn't return 0, but returns this Timestamp
     * for the InvoiceDTO class
     */
    @Autowired
    private @Value("${extractor.ignoreEarlier.invoices}") String ignoreInvoicesProperty = "2000-01-01";
    
    /** The extractor.ignoreEarlier.invoices property is formatted using this */
    private SimpleDateFormat ignoreEarlierFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /** {@link ignoreInvoicesProperty} converted to Timestamp */
    private Timestamp ignoreInvoicesFilter = null;

    /** Key for the map contains a store and a dtoClass */
    private static class Key {
        private SwarmStore store;
        private Class<? extends DWTransferable> dtoClass;
        
        public Key(SwarmStore store, Class<? extends DWTransferable> dtoClass) {
            this.store = store;
            this.dtoClass = dtoClass;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            
            if (obj == null){
                return false;
            }
            
            if (getClass() != obj.getClass()){
                return false;
            }
            
            Key other = (Key) obj;
            if (dtoClass == null) {
                if (other.dtoClass != null){
                    return false;
                }
            } else if (!dtoClass.equals(other.dtoClass)){
                return false;
            }
            
            if (store == null) {
                if (other.store != null){
                    return false;
                }
            } else if (!store.equals(other.store)){
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((dtoClass == null) ? 0 : dtoClass.hashCode());
            result = prime * result + ((store == null) ? 0 : store.hashCode());
            return result;
        }
    }
    
    /**
     * Cache is a dictionary from (SwarmStore, DTO class) to the most recent timestamp saved
     */
    private Map<Key, DWFilter> cache = new HashMap<Key, DWFilter>();
    
    /**
     * Register a list of dto's most recent timestmap (lastModified) in the cache
     * @param store The store that will be used as the key
     * @param dtoList The list of Data Transferable Objects that are saved in the data store
     */
    protected void registerInCache(SwarmStore store, List<? extends DWTransferable> dtoList, Class<? extends DWTransferable> dtoClass){
        // we require at least one item to avoid null pointer exception
        if(dtoList.isEmpty()) {
            return;
        }
        
        // key in the cache
        Key k = new Key(store, dtoClass);
        
        // value in the cache
        DWFilter max = (cache.containsKey(k)) ? cache.get(k) : new DWFilter();
        
        //iterate through each
        for(DWTransferable dto : dtoList) {
            Timestamp timestamp = dto.getLastModified();
            
            DWFilter filter = DWFilter.fromId(dto.getRemoteId());            
            if(timestamp != null){
                filter.setTime(timestamp);
            }

            if(max.compareTo(filter) < 0) {
                max = filter;
            }
        }
        
        //save
        cache.put(k, max);
    }
    
    /**
     * Get the most recent timestamp cached in the cache 
     * @param store The store for which this inquery is made
     * @param dtoClass The class of the dto's currently filtered
     * @returns The most recent timestamp, or null if none found
     */
    protected DWFilter getCachedFilter(SwarmStore store, Class<? extends DWTransferable> dtoClass){
        Key k = new Key(store, dtoClass);
        return (cache.containsKey(k)) ? cache.get(k) : null;
    }
    
    
    /**
     * Get the ignoreEarlier.invoices filter from properties
     * @returns Timestamp value of the swarm.property's extractor.ignoreEarlier.invoices
     */
    protected Timestamp getIgnoreInvoicesFilter(){
        if(this.ignoreInvoicesFilter == null){
            try {
                this.ignoreInvoicesFilter = new Timestamp(this.ignoreEarlierFormat.parse(this.ignoreInvoicesProperty).getTime());
            } catch (ParseException e) {
                LOGGER.debug("Failed to extractor.ignoreEarlier.invoices from properties, using 1970-01-01", e);
                this.ignoreInvoicesFilter = new Timestamp(0);
            }            
        }
        return this.ignoreInvoicesFilter;
    }
    
    /**
     * Clear cache
     */
    public void clearCache(){
        this.cache.clear();
    }
}
