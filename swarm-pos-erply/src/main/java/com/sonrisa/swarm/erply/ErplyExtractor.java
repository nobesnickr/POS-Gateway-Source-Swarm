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
package com.sonrisa.swarm.erply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.erply.dto.ErplyInvoiceDTO;
import com.sonrisa.swarm.erply.dto.ErplyInvoiceLineDTO;
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
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Erply extractor connects to Erply webservice and extracts
 * data from it. It transforms this data, and than saves it into 
 * the given data warehouse.
 * 
 * @author sonrisa
 */
@Component("ErplyExtractor")
public class ErplyExtractor extends BaseIteratingExtractor<ErplyAccount> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErplyExtractor.class);

    /** API reader */
    private ExternalAPIReader<ErplyAccount> apiReader;

    /** Initializes an instance of the MosExtractor class */
    @Autowired
    public ErplyExtractor(@Qualifier("erplyAPIReader") ExternalAPIReader<ErplyAccount> apiReader){ 
        super("com.sonrisa.swarm.erply.dto.Erply");
        this.apiReader = apiReader;
    }
	
    @Override
    protected void fetchInvoices(ErplyAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        
        Iterable<ExternalDTO> data = remoteRequest(InvoiceDTO.class, account, dataStore.getFilter(account, InvoiceDTO.class));

        ArrayList<ErplyInvoiceDTO> invoiceList = new ArrayList<ErplyInvoiceDTO>();
        ArrayList<ErplyInvoiceLineDTO> invoiceLineList = new ArrayList<ErplyInvoiceLineDTO>();

        // Iterate through all invoices
        for (ExternalDTO element : data) {
            double currencyRate = element.hasKey("currencyRate") ? element.getDouble("currencyRate") : 1.0;

            ErplyInvoiceDTO invoice = getDtoTransformer().transformDTO(element, ErplyInvoiceDTO.class);
            invoice.setTimeZone(account.getTimeZone());
            invoiceList.add(invoice);

            // invoice has array of rows for invoice lines
            int count = 0;
            for (ExternalDTO row : element.getNestedItems("rows")) {
                    ErplyInvoiceLineDTO lineItem = getDtoTransformer().transformDTO(row, ErplyInvoiceLineDTO.class);
                    lineItem.setCurrencyRate(currencyRate);
                    lineItem.setInvoiceId(invoice.getRemoteId());
                    lineItem.setLineId(count++);
                    lineItem.setTimestamp(invoice.getLastModified());
                    invoiceLineList.add(lineItem);
            }

            // if limit exceeded save them to staging tables
            if (invoiceList.size() > QUEUE_LIMIT) {
                dataStore.save(account, invoiceList, InvoiceDTO.class);
                dataStore.save(account, invoiceLineList, InvoiceLineDTO.class);
                invoiceLineList.clear();
                invoiceList.clear();
            }
        }

        // save to the data warehouse (at this point list shouldn't be much
        // longer then the queueLimit
        if(!invoiceList.isEmpty()){
            dataStore.save(account, invoiceList, InvoiceDTO.class);
            dataStore.save(account, invoiceLineList, InvoiceLineDTO.class);
        }
    }
	

    @Override
    protected void fetchInvoiceLines(ErplyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        return;
    }
    
    @Override
    protected void fetchManufacturers(ErplyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        return;
    }

    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, ErplyAccount account, DWFilter since) {
        
        // Retrieve only the recent items
        Map<String,String> params = new HashMap<String,String>();
        
        long lastModified = since.getTimestamp().getTime() / 1000; 
        params.put("changedSince", Long.toString(lastModified));
        
        String restUrl = "";
        if(clazz == InvoiceDTO.class){
            restUrl = "getSalesDocuments";
            params.put("getRowsForAllInvoices", "1");
        } else if(clazz == CategoryDTO.class){
            restUrl = "getProductCategories";
        } else if(clazz == ProductDTO.class){
            restUrl = "getProducts";
        } else if(clazz == CustomerDTO.class){
            restUrl = "getCustomers";
            params.put("responseMode", "detail");
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }

        final ExternalCommand<ErplyAccount> command = new ExternalCommand<ErplyAccount>(account, restUrl,params);
        SimpleApiRequest<ErplyAccount> request = new SimpleApiRequest<ErplyAccount>(apiReader, command);
        request.setFirstPage(ErplyAPI.FIRST_PAGE);
        
        return request;
    }


    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
