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
package com.sonrisa.swarm.kounta.extractor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Extractor for Kounta. This class is responsible for executing the batch extraction for invoices
 * meaning that the invoice's id, total and timestamp is to be written into the database,
 * with <code>lines_processed</code> set to false.
 * 
 * This class is thread safe.
 */
@Component("kountaExtractor")
public class KountaExtractor extends BaseIteratingExtractor<KountaAccount> {

 private static final Logger LOGGER = LoggerFactory.getLogger(KountaExtractor.class);
    
    /** API reader */
    private ExternalAPIReader<KountaAccount> apiReader;
    
    /** Initializes an instance of the KountaExtractor class */
    @Autowired
    public KountaExtractor(@Qualifier("kountaAPIReader") ExternalAPIReader<KountaAccount> apiReader){ 
        super("com.sonrisa.swarm.kounta.dto.Kounta");
        this.apiReader = apiReader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, KountaAccount account, DWFilter since) {

        Map<String, String> fields = new HashMap<String,String>();
        
        if(clazz != InvoiceDTO.class){
            throw new IllegalArgumentException("Illegal DTO instance for Kounta");
        }
        
        fields.put("created_gt", ISO8061DateTimeConverter.dateToString(since.getTimestamp(), "yyyy-MM-dd"));
        return new SimpleApiRequest<KountaAccount>(
                apiReader, new ExternalCommand<KountaAccount>(account, KountaUriBuilder.getSiteUri(account, "orders/complete.json"), fields));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchInvoiceLines(KountaAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No invoice lines for Kounta
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchCustomers(KountaAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No batch for customers for Kounta
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchProducts(KountaAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No batch for products for Kounta
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchManufacturers(KountaAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No manufacturers for Kounta pro
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchCategories(KountaAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No categories for Kounta
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
