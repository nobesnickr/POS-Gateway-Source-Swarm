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
package com.sonrisa.swarm.staging.converter.impl;

import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.util.InvoiceLineEntityUtil;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.staging.converter.InvoiceLineStagingConverter;
import com.sonrisa.swarm.staging.job.exception.AlreadyExistsException;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;

/**
 * Implementation of the {@link InvoiceLineStagingConverter} interface
 * @author sonrisa
 *
 */
@Service
public class InvoiceLineStagingConverterImpl extends BaseStagingConverterImpl<InvoiceLineStage, InvoiceLineEntity> implements InvoiceLineStagingConverter{

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceLineStagingConverterImpl.class);
    
    /** DAO of invoices in the data warehouse (aka legacy DB). */
    @Autowired
    private InvoiceLineDao dao;

    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private ProductDao productDao;

    @Autowired
    private InvoiceLineStagingService invoiceLineStagingService;  
    
    /**
     * {@inheritDoc }
     *
     * @param stageEntities
     */
    @Override
    public StageAndLegacyHolder<InvoiceLineStage, InvoiceLineEntity> convert(InvoiceLineStage stgLine) {
         final InvoiceLineEntity legacyEntity;
         try {
             legacyEntity = convertToLegacy(stgLine);
         } catch (AlreadyExistsException ex) {
             LOGGER.warn("InvoiceLine already exists: {}", stgLine, ex);
             return new StageAndLegacyHolder<InvoiceLineStage, InvoiceLineEntity>(stgLine, ex.getMessage());
         }
         return new StageAndLegacyHolder<InvoiceLineStage, InvoiceLineEntity>(legacyEntity, stgLine);
    }
    
    /**
     * Tries to save a new invoice line to the data warehouse.
     * 
     * @param stgLine
     * @return
     * @throws NumberFormatException 
     */
    private InvoiceLineEntity convertToLegacy(InvoiceLineStage stgLine) throws NumberFormatException, AlreadyExistsException {
        
        // check whether its store exists
        final StoreEntity store = invoiceLineStagingService.findStore(stgLine);  

        if (store == null) {
            LOGGER.debug("Staging invoice line can not be saved because its store does not exists: " + stgLine);
            return null;
        }        
        // OK, store exists
        final Long storeId = store.getId();

        // check whether its invoice exists
        final Long foreignInvoiceId = Long.valueOf(stgLine.getLsInvoiceId());
        final InvoiceEntity invoice = invoiceDao.findByStoreAndForeignId(storeId, foreignInvoiceId);           
        if (invoice == null) {
            LOGGER.debug("Staging invoice line can not be saved because its invoice (foreignId:"
                    + foreignInvoiceId + ") does not exists: " + stgLine);
            return null;
        }                 
        // OK, invoice exists
        final Long invoiceId = invoice.getId();
        
        // check whether its product exists
        ProductEntity product = null;
        Long foreignProductId = null;
        try {
        	foreignProductId = Long.valueOf(stgLine.getLsProductId());
        	product = productDao.findByStoreAndForeignId(storeId, foreignProductId);;
        } catch (NumberFormatException e){
            LOGGER.debug("Saving staging invoice encountered a problem because it's product is unknown for " + stgLine, e);
            foreignProductId = null;
            product = null;
        }

        // check whether this invoice line already has been moved to the data warehouse
        final Long foreignLineId = Long.valueOf(stgLine.getLsLineId());
        
        InvoiceLineEntity line = dao.findByStoreAndForeignId(storeId, invoiceId, foreignLineId);
        if (line == null) {
            line = new InvoiceLineEntity();

            // sets the reference to the store
            line.setStore(store);   
            // sets the reference to the invoice
            line.setInvoice(invoice);
        }
        
        // performs mapping between staging and destination line object
        dozerMapper.map(stgLine, line);
        
        // Copy description, price, category and manufacturer from product if missing on invoice line
        InvoiceLineEntityUtil.copyProductToInvoiceLine(line, product); 
        return line;
    }
}
