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
package com.sonrisa.swarm.mock;

import java.math.BigDecimal;
import java.util.Date;

import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.ProductStage;

/**
 * Mock file repository to make unit testing easier.
 *
 * @author joe
 */
public abstract class MockTestData { 
    
    public static final String MOCK_INVOICE = "01_mock_invoice.json";
    public static final String MOCK_INVOICE_ITEM = "02_mock_invoice_item.json";
    public static final String MOCK_CUSTOMER = "03_mock_customer.json";
    /** Mock RetailPro requests. */
    public static final String MOCK_REQUEST = "04_mock_request.json";
    public static final String MOCK_STORE = "05_mock_store.json";
    public static final String MOCK_STORE_MODIFIED = "06_mock_store2.json";
    public static final String MOCK_CLIENT_INFO = "07_mock_client_info.json";
    public static final String MOCK_CLIENT_INFO_MODIFIED = "08_mock_client_info2.json";
    
    /** A RetailPro invoice the gateway can receive. Its total value is too much to store in the legacy db. */
    public static final String TEST_RP_INVOICE = "17_invalid_rp_invoice_too_much_total.json";  
    
    /** A RetailPro invoice package the gateway can receive. The packaga contains an invalid invoice too. */
    public static final String TEST_RP_INVOICES_WITH_AN_INVALID = "18_rp_invoices_with_an_invalid.json";
        
    public static final String TEST_RP_INVOICES_WITH_INVALID_ITEMS = "19_rp_invoices_with_invalid_items.json";
    
    /** Two RetailPro invoice, one with a valid invoice number and an other with "0". */
    public static final String TEST_RP_INVOICES_WITH_NULL_NUMBER = "20_rp_invoices_with_null_number.json";
    
    /** Retail Pro invoice with 222,22 as Total */
    public static final String TEST_RP_INVOICES_WITH_HUNGARIAN_NUMBERS = "21_rp_invoices_with_hungarian_numbers.json";
    
    /** Retail Pro invoices with CreatedTime & CreatedDate set */
    public static final String TEST_RP_INVOICES_WITH_DOCTIME= "22_rp_invoices_with_doctime.json";
    
    /** Retail Pro invoice with 222,22 as Total */
    public static final String TEST_RP_INVOICES_WITH_MISSING_PRICE_FIELD= "23_rp_invoices_with_missing_price_field.json";
    
    /** Retail Pro invoice with "" as CustomerSid */
    public static final String TEST_RP_INVOICES_WITH_EMPTY_CUSTOMER_FIELD = "24_rp_invoices_with_empty_customer_field.json";
    
    /** Retail Pro store with long (more than 300) field values */ 
    public static final String MOCK_STORE_LONG_FIELDS = "33_mock_store_longfields.json";

    /** Retail Pro invoice with "" as CustomerSid */
    public static final String TEST_RP_WITH_RECEIPT_ATTRIBUTES = "35_rp_invoices_with_receipt_attributes.json";
    
    /** Retail Pro invoices with duplicated entities */
    public static final String TEST_RP_WITH_DUPLICATE_INVOICES = "43_mock_request_with_duplicates.json";
    
    /** Retail Pro uploaded log containing error */
    public static final String TEST_RP_LOG_WITH_ERROR = "36_mock_retailpro_client_error.log";
    
    /** Retail Pro uploaded log containing error message for missing Settings file */
    public static final String TEST_RP_LOG_WITH_SETTINGS_FILE_NOT_FOUND = "37_mock_retailpro_client_settings_error.log";
    
    /** Retail Pro JSON with really high product price */
    public static final String TEST_RP_WITH_PRODUCT_PRICE_TOO_MUCH = "52_rp_invoices_with_too_much_product_price.json";
    
    /** Retail Pro JSON with long (23 character) invoice price*/
    public static final String TEST_RP_WITH_INVOICE_PRICE_TOO_LONG = "53_rp_invoices_with_too_long_price.json";
    
    /** Retail Pro JSON with missing tax field*/
    public static final String TEST_RP_INVOICE_LINES_WITH_NULL_TAX = "54_rp_invoice_lines_with_no_tax.json";
    
    /** Retail Pro JSON with invoice date in the 17th century. (Wildlife sends these) */
    public static final String TEST_RP_INVOICES_WITH_DATE_CENTURIES_AGO = "55_rp_invoices_with_date_centuries_ago.json";
    
    /** Retail Pro uploaded log containing XML error */
    public static final String TEST_RP_LOG_WITH_SETTINGS_XML_ERROR = "58_mock_retailpro_client_xml_error.log";
    
    /** Retail Pro uploaded log containing XML error for Frye-Production */
    public static final String TEST_RP9_LOG_WITH_SETTINGS_XML_ERROR = "59_mock_retailpro9_client_xml_error.log";
    
    /**
     * Creates a mock {@link StoreEntity}.
     * 
     * @param name
     * @return 
     */
    public static StoreEntity mockStoreEntity(final String name){
        StoreEntity entity = new StoreEntity();
        entity.setActive(Boolean.TRUE);
        entity.setCreated(new Date());
        entity.setName(name);
        entity.setNotes("note");
        
        return entity;
    }
    
    /**
     * Creates a mock {@link CustomerStage} object.
     * This method does not sets the sbsNo,storeNo,swarmId triplet but sets the storeId,
     * so it creates a customer that looks like received from Erply.
     * 
     * @param lsCustomerId
     * @param name
     * @param storeId
     * @return 
     */
    public static CustomerStage mockCustomerStage(final String lsCustomerId, final String name, final Long storeId) {
        final CustomerStage mockCustomer = mockCustomerStage(null, null, null, lsCustomerId, name);
        mockCustomer.setStoreId(storeId);
        return mockCustomer;
    }
        
    
    /**
     * Creates a mock {@link CustomerStage} object.
     * This method sets the sbsNo,storeNo,swarmId triplet that identifies a RetailPro store.
     * So this methods creates a staging customer that looks like received from a RetailPro store.
     * 
     * @param swarmId
     * @param sbsNo
     * @param storeNo
     * @param lsCustomerId
     * @param name
     * @return 
     */
    public static CustomerStage mockCustomerStage(final String swarmId, final String sbsNo, final String storeNo, final String lsCustomerId, final String name) {
        final CustomerStage cust = new CustomerStage();
        
        cust.setName(name);
        cust.setFirstname("first" + name);
        cust.setLastname("last" + name);
        cust.setSwarmId(swarmId);
        cust.setLsSbsNo(sbsNo);
        cust.setLsStoreNo(storeNo);
        cust.setLsCustomerId(lsCustomerId);
        cust.setNotes("notes");
        cust.setPhone("phone");
        cust.setState("st");
        cust.setAddress1("addr1");
        cust.setAddress2("addr2");
        cust.setCity("city");
        cust.setEmail("customer@email.com");
        cust.setLastModified("1375103866");
        
        return cust;
    }
    
    /**
     * Creates a mock {@link InvoiceStage} object.
     * This method does not sets the sbsNo,storeNo,swarmId triplet but sets the storeId,
     * so it creates an invoice that looks like received from Erply.
     * 
     * @param lsInvoiecId
     * @param lsCustomerId
     * @param storeId
     * @param total
     * @return 
     */
    public static InvoiceStage mockInvoice(String lsInvoiecId, String lsCustomerId, Long storeId, String total){
        InvoiceStage inv = new InvoiceStage();
        inv.setInvoiceNo("121");
        inv.setLsCustomerId(lsCustomerId);
        inv.setLsInvoiceId(lsInvoiecId);
        inv.setStoreId(storeId);
        inv.setTotal(total);
        inv.setTs(mockTimestamp());
        
        return inv;
    }
    
    /**
     * Creates a mock {@link InvoiceStage} object.
     * This method sets the sbsNo,storeNo,swarmId triplet but doesn't set the storeId,
     * so it creates an invoice that looks like received from RetailPro.
     * 
     * @param lsInvoiecId
     * @param lsCustomerId
     * @param storeId
     * @param total
     * @return 
     */
    public static InvoiceStage mockInvoice(String swarmId, String sbsNo, String storeNo, String lsInvoiecId, String lsCustomerId, String total){
        InvoiceStage inv = new InvoiceStage();
        inv.setInvoiceNo("121");
        inv.setLsCustomerId(lsCustomerId);
        inv.setLsInvoiceId(lsInvoiecId);
        inv.setLsSbsNo(sbsNo);
        inv.setLsStoreNo(storeNo);
        inv.setSwarmId(swarmId);
        inv.setTotal(total);
        inv.setTs(mockTimestamp());
        
        return inv;
    }
    
    /**
     * Get mock unix timestamp as expected by the Dozer mapper
     * @return
     */
    private static final String mockTimestamp(){
        return Long.toString(new Date().getTime() * 1000L);
    }
    
    /**
     * Creates a mock {@link InvoiceLineStage} object.
     * 
     * @param invoice
     * @param prod
     * @param lineId
     * @return 
     */
    public static InvoiceLineStage mockInvoiceLine(InvoiceStage invoice, ProductStage prod, String lineId, Long storeId) {
        InvoiceLineStage line = new InvoiceLineStage();
        line.setLsInvoiceId(invoice.getLsInvoiceId());
        line.setLsLineId(lineId);
        line.setLsProductId(prod.getLsProductId());
        line.setPrice(prod.getPrice());
        line.setTax("25");        
        line.setQuantity("1");
        line.setStoreId(storeId);   
        return line;
    }
    
    /**
     * Creates a mock {@link InvoiceLineStage} object.
     * 
     * @param invoice
     * @param lineId
     * @return 
     */
    public static InvoiceLineStage mockInvoiceLine(InvoiceStage invoice, String lsProductId, String price, String lineId, Long storeId) {
        InvoiceLineStage line = new InvoiceLineStage();
        line.setLsInvoiceId(invoice.getLsInvoiceId());
        line.setLsLineId(lineId);
        line.setLsProductId(lsProductId);
        line.setPrice(price);
        line.setTax("0.25");        
        line.setQuantity("1");
        line.setStoreId(storeId);   
        return line;
    }
    
    /**
     * Creates a mock {@link CustomerEntity} object.
     * 
     * @param lsCustomerId
     * @param name
     * @param store
     * @return 
     */
    public static CustomerEntity mockCustomer(final Long lsCustomerId, final String name, final StoreEntity store) {
        final CustomerEntity cust = new CustomerEntity();
        
        cust.setName(name);
        cust.setFirstname("first" + name);
        cust.setLastname("last" + name);
        cust.setStore(store);
        cust.setLsCustomerId(lsCustomerId);
        cust.setNotes("notes");
        cust.setPhone("phone");
        cust.setState("st");
        cust.setAddress1("addr1");
        cust.setAddress2("addr2");
        cust.setCity("city");
        cust.setEmail("customer@email.com");
    
        return cust;
    }    
        
    /**
     * Creates a mock {@link ProductStage} staging entity that looks like received from Erply.
     * 
     * 
     * @param lsProductId
     * @param storeId
     * @return 
     */
    public static ProductStage mockProductStage(String lsProductId, Long storeId){
        final ProductStage prd = mockProductStage(null, null, null, lsProductId);
        prd.setStoreId(storeId);
        
        return prd;    
    }
    
    /**
     * Creates a mock {@link ProductStage} object.
     * 
     * @param swarmId
     * @param sbsNo
     * @param storeNo
     * @return 
     */    
    public static ProductStage mockProductStage(final String swarmId, final String sbsNo, final String storeNo, String lsProductId){
        ProductStage product = new ProductStage();
        product.setLsProductId(lsProductId);
        product.setSwarmId(swarmId);
        product.setLsStoreNo(storeNo);
        product.setLsSbsNo(sbsNo);
        product.setDescription("desc"+lsProductId);
        product.setCategory("category");
        product.setEan("ean");
        product.setManufacturer("manufacturer name");
        product.setModifiedAt("0");
        product.setPrice("0.99");
        product.setSku("sku");
        product.setStoreSku("storeSku");
        product.setUpc("upc");
        
        return product;
    }
    
    /**
     * Creates a mock {@link ProductEntity} object.
     * 
     * @param swarmId
     * @param sbsNo
     * @param storeNo
     * @return 
     */    
    public static ProductEntity mockProduct(Long lsProductId, StoreEntity strEntity){
        ProductEntity product = new ProductEntity();
        product.setLsProductId(lsProductId);
        product.setDescription("desc"+lsProductId);
        product.setCategory("category");
        product.setEan("ean");
        product.setManufacturer("manufacturer name");
        product.setLastModified(new Date());
        product.setPrice(new BigDecimal("44"));
        product.setSku("sku");
        product.setUpc("upc");        
        product.setStore(strEntity);
        return product;
    }    
    
    public static CategoryStage mockCategoryStage(final String categoryId, final Long storeId){
        CategoryStage category = new CategoryStage();
        category.setName("Category " + categoryId);
        category.setLsCategoryId(categoryId);
        category.setLastModified(Long.toString(new Date().getTime()));
        category.setStoreId(storeId);
        return category;
    }
    
    public static ManufacturerStage mockManufacturerStage(final String manufacturerId, final Long storeId){
        ManufacturerStage manufacturerStage = new ManufacturerStage();
        manufacturerStage.setLastModified(Long.toString(new Date().getTime()));
        manufacturerStage.setManufacturerId(manufacturerId);
        manufacturerStage.setManufacturerName("Manufacturer/" + manufacturerId);
        manufacturerStage.setStoreId(storeId);
        return manufacturerStage;
    }
    
}
