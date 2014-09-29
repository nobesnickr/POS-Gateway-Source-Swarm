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
package com.sonrisa.swarm.erply.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;


/**
 * Product retrieved from erply. Erply products carry information about their 
 * manufacturers and categories, but only textual data about manufacturers. 
 * Therefore the manufacturerID of the superclass will always be 0.
 */
public class ErplyProductDTO extends ProductDTO {

    /** The product id in the remote system */
    private long productId;
    
    /** Category of the product */
    private Long category = 0L;
    
    /** Category name of the product */
    private String categoryName = "";
    
    private String manufacturerName = "";
    
    /** Product description */
    private String description = "";
    
    /** Price of the product */
    private double price = 0.0;
    
    /** UPC */
    private String upc = "";
    
    /** EAN */
    private String ean ="";
    
    /** Stock Keeping Unit internally in the store */
    private String storeSku = "";
    
    /** Timestamp */
    private long lastModified = 0L;

    /**
     * @return the productId
     */
    public long getRemoteId() {
        return productId;
    }

    /**
     * @return the category
     */
    public Long getCategoryId() {
        return category;
    }

    /**
     * @return the categoryName
     */
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
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return the upc
     */
    public String getUpc() {
        return upc;
    }

    /**
     * @return the ean
     */
    public String getEan() {
        return ean;
    }

    /**
     * @return the storeSku
     */
    public String getStoreSku() {
        return storeSku;
    }

    /**
     * Get the timestamp of the last modification if the entry
     * 
     * This timestamp is calculated using the remote Unix timestamp of
     * Erply by multiplying it with 1000L, as java.sql.Timestamp expects
     * a timestamp with milliseconds
     * 
     * @return the lastModified
     */
    public Timestamp getLastModified() {
        return new Timestamp(lastModified * 1000L);
    }

    /**
     * @param productId the productId to set
     */
    @ExternalField(value = "productID", required = true)
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * @param category the category to set
     */
    @ExternalField("categoryID")
    public void setCategory(Long category) {
        this.category = category;
    }

    /**
     * @param categoryName the categoryName to set
     */
    @ExternalField("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @param manufacturerName the manufacturerName to set
     */
    @ExternalField("manufacturerName")
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * @param description the description to set
     */
    @ExternalField("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param price the price to set
     */
    @ExternalField("price")
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @param upc the upc to set
     */
    @ExternalField("code")
    public void setUpc(String upc) {
        this.upc = upc;
    }

    /**
     * @param ean the ean to set
     */
    @ExternalField("code2")
    public void setEan(String ean) {
        this.ean = ean;
    }

    /**
     * @param storeSku the storeSku to set
     */
    @ExternalField("code2")
    public void setStoreSku(String storeSku) {
        this.storeSku = storeSku;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("lastModified")
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the manufacturerId
     */
    @Override
    public Long getManufacturerId() {
        return 0L;
    }  
}
