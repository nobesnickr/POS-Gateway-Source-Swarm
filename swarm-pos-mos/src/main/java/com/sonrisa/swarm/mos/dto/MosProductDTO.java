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
package com.sonrisa.swarm.mos.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MosProductDTO extends ProductDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MosProductDTO.class);

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
        return description;
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
    @ExternalField("itemID")
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * @param category the category to set
     */
    @ExternalField("categoryID")
    public void setCategory(String category) {
        try {
            this.category = Long.parseLong(category);
        } catch (NumberFormatException e){
            LOGGER.debug("Failed to parse category {}", category, e);
            this.category = null;
        }
    }

    /**
     * @param manufacturer the manufacturerName to set
     */
    @ExternalField("manufacturerID")
    public void setManufacturer(String manufacturer) {
        try {
            this.manufacturerId = Long.parseLong(manufacturer);
        } catch (NumberFormatException e){
            LOGGER.debug("Failed to parse manufacturer {}", manufacturer, e);
            this.manufacturerId = null;
        }
    }

    /**
     * @param description the description to set
     */
    @ExternalField("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param prices List of the prices to be set
     */
    @ExternalField("Prices")
    public void setPrice(ExternalDTO prices) throws ExternalExtractorException {
        for(ExternalDTO priceRow : prices.getNestedItems("ItemPrice")){
            try {
                price = priceRow.getDouble("amount");
            } catch (ExternalExtractorException e) {
                LOGGER.debug("Failed to parse price from {}", priceRow, e);
                price = 0.0;
            }
            if(priceRow.getText("useType").equalsIgnoreCase("Default")){
                return;
            }
        }
    }

    /**
     * @param upc the UPC to set
     */
    @ExternalField("upc")
    public void setUpc(String upc) {
        this.upc = upc;
    }

    /**
     * @param ean the ean to set
     */
    @ExternalField("ean")
    public void setEan(String ean) {
        this.ean = ean;
    }

    /**
     * @param storeSku the storeSku to set
     */
    @ExternalField("customSku")
    public void setStoreSku(String storeSku) {
        this.storeSku = storeSku;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("timeStamp")
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
