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
package com.sonrisa.swarm.warehouse.stage;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.OutletDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.dto.RegisterDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.annotation.DWFilteredAs;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.util.BaseCachingAndIgnoringDTOService;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.OutletStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;
import com.sonrisa.swarm.staging.service.RegisterStagingService;

/**
 * Staging data store is an implementation of a SwarmDataStore, so it is 
 * capable of accepting DTOs (Data Transfer Objects), and saving them.
 * This particular DataStore uses staging tables to save data into.
 * @author sonrisa
 *
 */
@Service
public class StagingDTOService extends BaseCachingAndIgnoringDTOService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StagingDTOService.class);
    
	/** The staging data save service that saves data into the staging tables	 */
	@Autowired
	private CustomerStagingService customerStgService;
	@Autowired
	private ProductStagingService productStgService;
	@Autowired
	private InvoiceStagingService invoiceStgService;
	@Autowired
	private InvoiceLineStagingService invoiceLineStgService;
    @Autowired
    private CategoryStagingService categoryStgService;
    @Autowired
    private ManufacturerStagingService manufacturerStgService;
    @Autowired
    private OutletStagingService outletStgService;
    @Autowired
    private RegisterStagingService registerStgService;
    
	/** JDBC template is required to access the legacy databases's updates table */
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
    
    /**
     * Save DTO entities into staging tables
     * 
     * @param store Store contains the local store_id
     * @param entities List of DTO entities to be saved into stage tables
     * @param clazz Type of the DTO to know which table to save into
     * @param <T> Type of the DTO, each item in the List should be of the same type
     */
    public <T extends DWTransferable> void saveToStage(SwarmStore store, List<? extends T> entities, Class<T> clazz) {
        
        LOGGER.debug("Inserting {} {} entities into staging tables for store {}", entities.size(), clazz.getSimpleName(), store.getStoreId());

        if(clazz == InvoiceDTO.class){
            invoiceStgService.create(entities, store.getStoreId());
        } else if(clazz == InvoiceLineDTO.class){
            invoiceLineStgService.create(entities, store.getStoreId());
        } else if(clazz == ManufacturerDTO.class){
            manufacturerStgService.create(entities, store.getStoreId());
        } else if(clazz == CustomerDTO.class){
            customerStgService.create(entities, store.getStoreId());
        } else if(clazz == ProductDTO.class){
            productStgService.create(entities, store.getStoreId());
        } else if(clazz == CategoryDTO.class){
            categoryStgService.create(entities, store.getStoreId());
        } else if(clazz == OutletDTO.class){
        	outletStgService.create(entities, store.getStoreId());
        } else if(clazz == RegisterDTO.class){
        	registerStgService.create(entities, store.getStoreId());
        } else {
            throw new RuntimeException("Unexpected entity type!");
        }
        
        // Register should happen only if moving into the stage tables
        // was successfully finished.
        this.registerInCache(store, entities, clazz);
    }

	/**
	 * To filter the data fetched from the external data source, the extractor
	 * passes a timestamp to the
	 */
	public DWFilter getFilter(SwarmStore store, Class<? extends DWTransferable> dtoClass) {
	    DWFilter cachedValue = this.getCachedFilter(store, dtoClass); 
	    if(cachedValue != null) {
	        return cachedValue;
	    }
	    
	    Class<? extends DWTransferable> legacyTsClass = getTimestampClass(dtoClass);
	    
	    StageInsertableType classAnnotation = legacyTsClass.getAnnotation(StageInsertableType.class);
	    
	    if(classAnnotation == null){
	        throw new UnsupportedOperationException(legacyTsClass.getSimpleName() + " class is not correctly annotated using the StageInsertableType annotation.");
	    }
	    
	    // get table name from class's annotation
	    String tableName = classAnnotation.dbTableName();
	    
	    // try to find the column annotated as usedAsTimestamp
	    Method[] methods = legacyTsClass.getMethods();
	    String timeStampColumn = null;
	    String idColumn = null;
	    for(Method method : methods){
            if(method.isAnnotationPresent(StageInsertableAttr.class)){
                StageInsertableAttr annotation = method.getAnnotation(StageInsertableAttr.class);
                if(annotation.usedAsTimestamp()){
                    timeStampColumn = method.getAnnotation(StageInsertableAttr.class).dbColumnName();
                }
                if(annotation.usedAsRemoteId()){
                    idColumn = method.getAnnotation(StageInsertableAttr.class).dbColumnName();
                }
            }
	    }
	    
	    //if none found with timestamp annotation
	    if(timeStampColumn == null){
	        throw new UnsupportedOperationException(legacyTsClass.getSimpleName() + " class is not correctly annotated, missing the usedAsTimestemp field.");
	    }
	    
	    Map<String,Object> retval = null;
	    final String query = 
	            "SELECT MAX("+ timeStampColumn + ") AS " + timeStampColumn + 
                        ((idColumn != null) ? ", MAX("+ idColumn + ") AS " + idColumn : "") + 
                        " FROM " + tableName
                         + " WHERE store_id = ?;";
	    
	    retval = jdbcTemplate.queryForMap(query,new Object[]{ (Long) store.getStoreId() });
		
		if(retval != null){
		    DWFilter filter = new DWFilter();
		    boolean atLeastOne = false;
		    if(retval.containsKey(idColumn) && retval.get(idColumn) != null){
		        filter.setId((Long)retval.get(idColumn));
		        atLeastOne = true;
		    }
		    if(retval.containsKey(timeStampColumn) && retval.get(timeStampColumn) != null){
		        filter.setTime((Timestamp)retval.get(timeStampColumn));
		        atLeastOne = true;
		    }
		    if(atLeastOne){
		        return filter;
		    }
		}

		// For invoices ignore earlier invoices than a certain filter
		// set in swarm.properties
		if (legacyTsClass == InvoiceDTO.class || legacyTsClass == InvoiceLineDTO.class){
		    return DWFilter.fromTimestamp(this.getIgnoreInvoicesFilter());
		}
		
		return new DWFilter();
	}
	

    /**
     * Returns the class the DTO is transfered as
     * @param clazz
     * @return
     */
    private static <T extends DWTransferable> Class<? extends DWTransferable> getTimestampClass(Class<T> clazz){
        DWFilteredAs annotation = clazz.getAnnotation(DWFilteredAs.class);
        if(annotation != null){
            return annotation.value();
        }
        return clazz;
    }

    @Override
    public String toString() {
        return "StagingDataStore";
    }}
