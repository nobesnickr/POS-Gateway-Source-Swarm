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
package com.sonrisa.swarm.posintegration.extractor.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorNamingConventionException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import java.util.List;

/**
 * Base classes for Extractors operating with an external API
 *
 * @param <T> SwarmStore implementation carrying the authentication information for the API
 */
public abstract class BaseIteratingExtractor<T extends SwarmStore> implements ExternalExtractor<T>{

    /** Service transforming ExternalDTO objects into DataStoreTransferable entities */
    @Autowired
    private ExternalDTOTransformer dtoTransformer;
    
    /**
     * E.g. com.sonrisa.swarm.erply.dto.Erply
     */
    private String dtoClassPrefix = "";

    /**
     * If either queue is longer than this value it is saved immediately, even if 
     * more data from the same DTO is available.
     */
    public static final int QUEUE_LIMIT = 200;
    
    /**
     * Initialize by setting the path to the POS specific DTOs
     * @param dtoClassPrefix E.g. com.sonrisa.swarm.erply.dto.Erply
     */
    protected BaseIteratingExtractor(String dtoClassPrefix){
        this.dtoClassPrefix = dtoClassPrefix;
    }

    /**
     * {@inheritDoc}
     * 
     * Iterates through all the accounts, and fetch data from the API
     */
    @Override
    public void fetchData(T account, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        logger().debug("Importing data for: {}", account);

        fetchCategories(account, dataStore);
        fetchProducts(account, dataStore);
        fetchInvoices(account, dataStore);
        fetchInvoiceLines(account, dataStore);
        fetchCustomers(account, dataStore);
        fetchManufacturers(account, dataStore);
    }
    
    /**
     * Fetches the POS specific {@link CategoryDTO}
     */
    protected void fetchCategories(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,CategoryDTO.class,getPosSpecificClass(CategoryDTO.class));
    }
    
    /**
     * Fetches the POS specific {@link ProductDTO}
     */
    protected void fetchProducts(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,ProductDTO.class,getPosSpecificClass(ProductDTO.class));
    }
    
    /**
     * Fetches the POS specific {@link InvoiceDTO}
     */
    protected void fetchInvoices(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,InvoiceDTO.class,getPosSpecificClass(InvoiceDTO.class));
    }
    
    /**
     * Fetches the POS specific {@link InvoiceLineDTO}
     */
    protected void fetchInvoiceLines(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,InvoiceLineDTO.class,getPosSpecificClass(InvoiceLineDTO.class));
    }
    
    /**
     * Fetches the POS specific {@link CustomerDTO}
     */
    protected void fetchCustomers(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,CustomerDTO.class,getPosSpecificClass(CustomerDTO.class));
    }

    /**
     * Fetches the POS specific {@link ManufacturerDTO}
     */
    protected void fetchManufacturers(T store, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        fetchRemoteData(store,dataStore,ManufacturerDTO.class,getPosSpecificClass(ManufacturerDTO.class));
    }
    
    /**
     * Fired when the iteration needs a new kind of data, e.g. CustomerDTO
     * @param clazz Type of warehouse DTO
     * @param since Filtering based on this value
     * @return
     */
    protected abstract Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, T account, DWFilter since);
    
    /**
     * Fetch data from Merchant OS REST server
     * 
     * Example: fetchRemoteData(mosStore, stagingDataStore, "SaleLine", MosInvoiceLineDTO.class, InvoiceLineDTO.class, InvoiceDTO.class)
     * 
     * @param store Store identifying the data received in the stage tables
     * @param dataStore Data store (e.g. Staging Data Store) that saves the received data
     * @param restURL Rest URL providing access to the certain data type
     * @param clazz Class of the DTO expected from the REST URL request
     * @param timeStampClass Indicates which DTO class's timeStamp is used for filtering remote data
     */
    private <W extends DWTransferable, S extends W>void fetchRemoteData(
            T store, SwarmDataWarehouse dataStore, Class<W> dataStoreClass, Class<S> clazz){

        DWFilter filter = dataStore.getFilter(store, dataStoreClass);

        List<W> itemList = new ArrayList<W>();
        Iterable<ExternalDTO> data = remoteRequest(dataStoreClass, store, filter);
 
        for(ExternalDTO node : data){
            try {
                W item = this.dtoTransformer.transformDTO(node, clazz);
                itemList.add(item);
                
                // If list reaches a limit, save items into the stage, and 
                // clear the list
                if(itemList.size() > QUEUE_LIMIT){
                    dataStore.save(store, itemList, dataStoreClass);
                    itemList.clear();
                }
            } catch (ExternalExtractorException extractorException){
                logger().warn("Error occured while trying to extract data from " + node, extractorException);
            }
        }
        
        if(!itemList.isEmpty()){
            dataStore.save(store, itemList, dataStoreClass);
        }
    }
    
    protected abstract Logger logger();
        
    
    /**
     * Returns the POS specific implementation of a class, e.g. CustomerDTO -> ErplyCustomerDTO
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends DWTransferable> Class<? extends T> getPosSpecificClass(Class<T> clazz){
        Class<?> retval;
        try {
            retval = Class.forName(this.dtoClassPrefix + clazz.getSimpleName());
        } catch (ClassNotFoundException e) {
            throw new ExternalExtractorNamingConventionException(e);
        }
        
        if(!clazz.isAssignableFrom(retval)){
            throw new ExternalExtractorNamingConventionException("Not superclass for " + clazz.getSimpleName() + " at " + this.dtoClassPrefix + clazz.getSimpleName());            
        } 
        return (Class<? extends T>)retval;
    }
    
    protected ExternalDTOTransformer getDtoTransformer() {
        return dtoTransformer;
    }

    public void setDtoTransformer(ExternalDTOTransformer dtoTransformer) {
        this.dtoTransformer = dtoTransformer;
    }
}
