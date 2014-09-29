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
package com.sonrisa.swarm.revel;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.revel.dto.RevelInvoiceDTO;


/**
 * Implementation of the {@link ExternalExtractor} interface for {@link RevelAccount} stores
 */
@Component("RevelExtractor")
public class RevelExtractor extends BaseIteratingExtractor<RevelAccount>{
    private static final Logger LOGGER = LoggerFactory.getLogger(RevelExtractor.class);

    /** API reader */
    private ExternalAPIReader<RevelAccount> apiReader;
    
    /** Initializes an instance of the MosExtractor class */
    @Autowired
    public RevelExtractor(@Qualifier("revelAPIReader") ExternalAPIReader<RevelAccount> apiReader){ 
        super("com.sonrisa.swarm.revel.dto.Revel");
        this.apiReader = apiReader;
    }
    
    @Override
    protected void fetchManufacturers(RevelAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No manufacturers for Revel
        return;
    }

    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, RevelAccount account, DWFilter since) {
        
        Map<String, String> fields = new HashMap<String,String>();
        fields.put("updated_date__gte", ISO8061DateTimeConverter.dateToMysqlString(new Date(since.getTimestamp().getTime())));
        
        if(account.hasStoreFilter()){
            fields.put("establishment", account.getStoreFilter());
        }
 
        String restUrl;
        if(clazz == CategoryDTO.class){
            restUrl = "products/ProductCategory";
        } else if(clazz == ProductDTO.class){
            restUrl = "resources/Product";
        } else if(clazz == CustomerDTO.class){
            restUrl = "resources/Customer";
        } else if(clazz == InvoiceDTO.class){
            restUrl = "resources/Order";
        } else if(clazz == InvoiceLineDTO.class){
            restUrl = "resources/OrderItem";
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        LOGGER.debug("Sending request to {} with {}", restUrl, account);
        return new SimpleApiRequest<RevelAccount>(apiReader, new ExternalCommand<RevelAccount>(account, restUrl, fields));
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchInvoices(RevelAccount store, SwarmDataWarehouse dataStore){

        DWFilter filter = dataStore.getFilter(store, InvoiceDTO.class);

        List<InvoiceDTO> itemList = new ArrayList<InvoiceDTO>();
        Iterable<ExternalDTO> data = remoteRequest(InvoiceDTO.class, store, filter);
 
        for(ExternalDTO node : data){
            try {
                RevelInvoiceDTO item = getDtoTransformer().transformDTO(node, RevelInvoiceDTO.class);
                item.setTimezone(store.getTimeZone());
                itemList.add(item);
                
                // If list reaches a limit, save items into the stage, and 
                // clear the list
                if(itemList.size() > QUEUE_LIMIT){
                    dataStore.save(store, itemList, InvoiceDTO.class);
                    itemList.clear();
                }
            } catch (ExternalExtractorException extractorException){
                logger().warn("Error occured while trying to extract data from " + node, extractorException);
            }
        }
        
        if(!itemList.isEmpty()){
            dataStore.save(store, itemList, InvoiceDTO.class);
        }
    }
}
