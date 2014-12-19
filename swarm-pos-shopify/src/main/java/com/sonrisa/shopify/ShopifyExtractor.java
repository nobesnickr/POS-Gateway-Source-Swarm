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
package com.sonrisa.shopify;

import java.util.ArrayList;
import java.util.Date;
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
import com.sonrisa.swarm.shopify.dto.ShopifyInvoiceDTO;
import com.sonrisa.swarm.shopify.dto.ShopifyInvoiceLineDTO;
import com.sonrisa.swarm.shopify.dto.ShopifyProductDTO;

/**
 * Extractor for {@link ShopifyAccount} stores.
 * 
 * @author sonrisa
 */
@Component("ShopifyExtractor")
public class ShopifyExtractor extends BaseIteratingExtractor<ShopifyAccount>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyExtractor.class);

    /** API reader */
    private ExternalAPIReader<ShopifyAccount> apiReader;
    
    private static final String TOTAL_TAX_JSON_KEY = "total_tax";
    
    @Autowired
    public ShopifyExtractor(@Qualifier("shopifyAPIReader") ExternalAPIReader<ShopifyAccount> apiReader){
        super("com.sonrisa.swarm.shopify.dto.Shopify");
        this.apiReader = apiReader;
    }
    
    @Override
    protected void fetchInvoiceLines(ShopifyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        // InvoiceLine is fetched from invoices
    }
    
    @Override
    protected void fetchManufacturers(ShopifyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        // No manufacturers for Shopify
    }

    @Override
    protected void fetchCategories(ShopifyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
       // No categories for shopify
    }
    
    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, ShopifyAccount account, DWFilter since) {

        // Retrieve only the recent items
        Map<String,String> params = new HashMap<String,String>();
        
        String restUrl;
        if(clazz == CustomerDTO.class){
            restUrl = "customers.json";
            params.put("since_id", Long.toString(since.getId()));
        } else if(clazz == InvoiceDTO.class){
            restUrl = "orders.json";
            params.put("updated_at_min", ISO8061DateTimeConverter.dateToMySqlStringWithTimezone(new Date(since.getTimestamp().getTime())));
            params.put("status", "any");
        } else if(clazz == ProductDTO.class){
            restUrl = "products.json";
            params.put("updated_at_min", ISO8061DateTimeConverter.dateToMySqlStringWithTimezone(new Date(since.getTimestamp().getTime())));
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        return new SimpleApiRequest<ShopifyAccount>(apiReader, new ExternalCommand<ShopifyAccount>(account,restUrl,params));
    }
    
    /**
     * Fetch product (variants) from Shopify
     */
    @Override
    protected void fetchProducts(ShopifyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        DWFilter filter = dataStore.getFilter(store, InvoiceDTO.class);

        List<ProductDTO> productList = new ArrayList<ProductDTO>();
        
        Iterable<ExternalDTO> data = remoteRequest(ProductDTO.class, store, filter);
 
        for(ExternalDTO topNode : data){
            for(ExternalDTO node : topNode.getNestedItems("variants")){
                try {
                    ShopifyProductDTO item = getDtoTransformer().transformDTO(node, ShopifyProductDTO.class);
                    
                    final String keyForProductType = "product_type";
                    if(topNode.hasKey(keyForProductType)){
                        item.setCategory(topNode.getText(keyForProductType));
                    }
                    final String keyForTitle = "title";
                    if(topNode.hasKey(keyForTitle)){
                        item.setTopDescription(topNode.getText(keyForTitle));
                    }
                    productList.add(item);
                    
                    // If list reaches a limit, save items into the stage, and 
                    // clear the list
                    if(productList.size() > QUEUE_LIMIT){
                        dataStore.save(store, productList, ProductDTO.class);
                        productList.clear();
                    }
                } catch (ExternalExtractorException extractorException){
                    logger().warn("Error occured while trying to extract data from " + node, extractorException);
                }
            }
        }
        
        if(!productList.isEmpty()){
            dataStore.save(store, productList, ProductDTO.class);
        }
    }
    
    /**
     * Fetch invoices from Shopify
     */
    @Override
    protected void fetchInvoices(ShopifyAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        DWFilter filter = dataStore.getFilter(store, InvoiceDTO.class);

        List<InvoiceDTO> itemList = new ArrayList<InvoiceDTO>();
        List<InvoiceLineDTO> invoiceLineList = new ArrayList<InvoiceLineDTO>();
        
        Iterable<ExternalDTO> data = remoteRequest(InvoiceDTO.class, store, filter);
 
        for(ExternalDTO node : data){
            try {
                ShopifyInvoiceDTO item = getDtoTransformer().transformDTO(node, ShopifyInvoiceDTO.class);
                itemList.add(item);
                
                double totalTax = 0.0;
                if (node.hasKey(TOTAL_TAX_JSON_KEY)) {
                        totalTax = node.getDouble(TOTAL_TAX_JSON_KEY);
                }

                double total = item.getLineNetTotal();
                
                for(ExternalDTO lineItem : node.getNestedItems("line_items")){
                    ShopifyInvoiceLineDTO invoiceLine = getDtoTransformer().transformDTO(lineItem, ShopifyInvoiceLineDTO.class);
                    invoiceLine.setInvoiceId(item.getRemoteId());

                    // Line based taxes are not supported in shopify, so we estimate
                    // the tax rate from the totalTax and totalPrice of the invoice
                    if (total > 0.0) {
                            invoiceLine.setTax(totalTax / total * invoiceLine.getNetPrice());
                    }

                    invoiceLineList.add(invoiceLine);
                }
                
                // If list reaches a limit, save items into the stage, and 
                // clear the list
                if(itemList.size() > QUEUE_LIMIT){
                    dataStore.save(store, itemList, InvoiceDTO.class);
                    dataStore.save(store, invoiceLineList, InvoiceLineDTO.class);
                    itemList.clear();
                    invoiceLineList.clear();
                }
            } catch (ExternalExtractorException extractorException){
                logger().warn("Error occured while trying to extract data from " + node, extractorException);
            }
        }
        
        if(!itemList.isEmpty()){
            dataStore.save(store, itemList, InvoiceDTO.class);
            dataStore.save(store, invoiceLineList, InvoiceLineDTO.class);
        }
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
