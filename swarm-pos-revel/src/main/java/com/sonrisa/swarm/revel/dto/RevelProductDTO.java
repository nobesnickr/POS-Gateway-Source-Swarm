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
package com.sonrisa.swarm.revel.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.revel.exception.RevelResourcePathFormatException;
import com.sonrisa.swarm.revel.util.RevelResourcePathConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Product from Revel
 *
 */
public class RevelProductDTO extends ProductDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(RevelProductDTO.class);

    /** The product id in the remote system */
    private long productId;
    
    /** Category of the product */
    private Long category = 0L;
    
    /** Product description */
    private String description = "";
    
    /** Price of the product */
    private double price = 0.0;
    
    /** UPC */
    private String upc = "";
    
    /** Timestamp of the product*/
    private Timestamp lastModified = new Timestamp(0L);

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
        return "";
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
        return "";
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
        return "";
    }

    /**
     * @return the storeSku
     */
    @Override
    public String getStoreSku() {
        return "";
    }

    /**
     * Get the timestamp of the last modification of the entry
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @param productId the productId to set
     */
    @ExternalField(value = "id", required = true)
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * @param categoryPath REST URL for the category
     */
    @ExternalField("category")
    public void setCategory(String categoryPath) {
        try {
            this.category = RevelResourcePathConverter.resourcePathToLong("/products/ProductCategory/", categoryPath);
        } catch (RevelResourcePathFormatException e){
            LOGGER.debug("Failed to parse category from: {}", categoryPath, e);
            this.category = 0L;
        }
    }

    /**
     * @param name the description to set
     */
    @ExternalField("name")
    public void setDescription(String name) {
        this.description = name;
    }

    /**
     * @return the manufacturerId
     */
    @Override
    public Long getManufacturerId() {
        return 0L;
    }  
    
    /**
     * @param price the price to set
     */
    @ExternalField("price")
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @param barcode the UPC to set
     */
    @ExternalField("barcode")
    public void setUpc(String barcode) {
        this.upc = barcode;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("updated_date")
    public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
    }
}
