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

package com.sonrisa.swarm.lspro.extractor;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.lspro.LsProAccount;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Extractor for Lightspeed Pro. This class is responsible for executing 
 * an extraction session during which the recent sales entities (Invoices, Products, etc.)
 * are fetched from the REST server and moved to the data warehouse.
 * 
 * This class is thread safe.
 */
@Component("lsProExtractor")
public class LsProExtractor extends BaseIteratingExtractor<LsProAccount> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LsProExtractor.class);
    
    /** API reader */
    private ExternalAPIReader<LsProAccount> apiReader;
    
    private static final String DATE_MODIFIED_FIELD = "DateModified";
    
    private static final String DATE_CREATED_FIELD = "DateCreatedUtc";

    /** Initializes an instance of the MosExtractor class */
    @Autowired
    public LsProExtractor(@Qualifier("lsProAPIReader") ExternalAPIReader<LsProAccount> apiReader){ 
        super("com.sonrisa.swarm.lspro.dto.LsPro");
        this.apiReader = apiReader;
    }

    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, LsProAccount account, DWFilter since) {
        
        Map<String, String> fields = new HashMap<String,String>();
        
 
        String restUrl;
        if(clazz == ProductDTO.class){
            restUrl = "Products";
            fields.putAll(createFieldsByFilter(DATE_MODIFIED_FIELD, since, account.getStoreFilter()));
            fields.putAll(createFieldsForOrderAsc(DATE_MODIFIED_FIELD));
            
        } else if(clazz == CustomerDTO.class){
            restUrl = "Customers";
            fields.putAll(createFieldsByFilter(DATE_MODIFIED_FIELD, since, account.getStoreFilter()));
            fields.putAll(createFieldsForOrderAsc(DATE_MODIFIED_FIELD));
            
        } else if(clazz == InvoiceDTO.class){
            restUrl = "Invoices";
            fields.putAll(createFieldsByFilter(DATE_CREATED_FIELD, since, account.getStoreFilter()));
            fields.putAll(createFieldsForOrderAsc(DATE_CREATED_FIELD));
            
            // Normally the Invoices JSON doesn't contain a customer id,
            // so we will force it to be included
            fields.put("$expand", "Customer");
            
        } else if(clazz == InvoiceLineDTO.class){
            restUrl = "LineItems";
            fields.putAll(createFieldsByFilter(DATE_CREATED_FIELD, since, account.getStoreFilter()));
            fields.putAll(createFieldsForOrderAsc(DATE_CREATED_FIELD));
            
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        LOGGER.debug("Sending request to {} with {}", restUrl, account);
        
        return new SimpleApiRequest<LsProAccount>(apiReader, new ExternalCommand<LsProAccount>(account, restUrl, fields));
    }

    /**
     * Generates URL GET parameter which will filter out entities 
     * which are already in the legacy tables (or cache).
     * 
     * @param since
     * @return
     */
    private Map<String, String> createFieldsByFilter(String filterField, DWFilter since, String location) {
        Map<String, String> fields = new HashMap<String,String>();
        
        StringBuilder filterString = new StringBuilder();

        /*
         * We want timestamps like this 
         * "Products?$filter=(DateModified)gt(DateTime'2014-01-01')
         */
        // "gt" is Greater then (ge would be greater then or equal)
        filterString.append('(').append(filterField).append(")gt(DateTime'");
        
        // dates as stores in the local timezone of the to store
        filterString.append(ISO8061DateTimeConverter.dateToOdataString(new Date(since.getTimestamp().getTime())));
        filterString.append("')");
        
        if(StringUtils.hasLength(location)){
            filterString.append("and(LocationName)eq('").append(location).append("')");
        }
        
        fields.put("$filter", filterString.toString());
        return fields;
    }
    
    /**
     * Generates URL GET parameter which will order the entities 
     * 
     * @param orderBy
     * @return
     */
    private Map<String,String> createFieldsForOrderAsc(String orderBy){
        Map<String, String> fields = new HashMap<String,String>();

        /*
         * We want order key like this
         * $orderby=(DateModified)asc")));
         */
        StringBuilder orderString = new StringBuilder();
        orderString.append('(').append(orderBy).append(")asc");
        
        fields.put("$orderby", orderString.toString());
        return fields;
    }
    
    @Override
    protected void fetchManufacturers(LsProAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No manufacturers for Lightspeed pro
        return;
    }
    

    @Override
    protected void fetchCategories(LsProAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No categories for Lightspeed pro
        return;
    }
    
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
