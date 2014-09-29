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
package com.sonrisa.swarm.shopify.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

public class ShopifyProductDTO extends ProductDTO {

    /** The product id in the remote system */
    private long productId;
    
    /** Category of the product */
    private Long category;
    
    /** Category name of the product */
    private String categoryName;
    
    /** Identifier of the manufacturer in the foreign system */
    private Long manufacturerId;
    
    /** Name of the manufacturer in the foreign system */
    private String manufacturerName;
    
    /** Product description */
    private String topDescription;
    
    /** Product description */
    private String description;
    
    /** Price of the product */
    private double price;
    
    /** UPC */
    private String upc;
    
    /** EAN */
    private String ean;
    
    /** Stock Keeping Unit internally in the store */
    private String storeSku;
    
    /** Timestamp */
    private Timestamp lastModified;

    /**
     * @return the productId
     */
    @Override
    public long getRemoteId() {
        return productId;
    }

    /**
     * @return the category
     */
    @Override
    public Long getCategoryId() {
        return category;
    }

    /**
     * @return the categoryName
     */
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @return the manufacturer
     */
    public Long getManufacturer() {
        return 0L;
    }

    /**
     * @return the manufacturerName
     */
    @Override
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return topDescription + " " + description;
    }

    /**
     * @return the price
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * @return the UPC
     */
    @Override
    public String getUpc() {
        return upc;
    }

    /**
     * @return the EAN
     */
    @Override
    public String getEan() {
        return ean;
    }

    /**
     * @return the storeSku
     */
    @Override
    public String getStoreSku() {
        return storeSku;
    }

    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @param productId the productId to set
     */
    @ExternalField("id")
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * TODO: category = product_type?
     * @param productType the category to set
     */
    public void setCategory(String productType) {
        this.categoryName = productType;
    }

    /**
     * manufacturer = vendor?
     * @param manufactorer the manufacturerName to set
     */
    public void setManufacturer(String manufactorer) {
        this.manufacturerName = manufactorer;
    }

    @ExternalField("title")
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setTopDescription(String topDescription) {
        this.topDescription = topDescription;
    }

    /**
     * @param price the price to set
     */
    @ExternalField("price")
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @param upc the UPC to set
     */
    public void setUpc(String upc) {
        this.upc = upc;
    }


    /**
     * @param storeSku the storeSku to set
     */
    @ExternalField("sku")
    public void setStoreSku(String storeSku) {
        this.storeSku = storeSku;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("updated_at")
    public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
    }

    /**
     * @return the manufacturerId
     */
    @Override
    public Long getManufacturerId() {
        return manufacturerId;
    }  
}
