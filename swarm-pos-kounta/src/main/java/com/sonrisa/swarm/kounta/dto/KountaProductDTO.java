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
package com.sonrisa.swarm.kounta.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Kounta product
 * 
 * @author Barnabas
 */
public class KountaProductDTO extends ProductDTO {

    /**
     * Foreign id
     */
    private long id;
    
    /**
     * Product's name
     */
    private String name;
    
    /**
     * Last known paid price for this item
     */
    private double unitPrice;
    
    /**
     * Last invoice's timestamp where this product occurs
     */
    private Timestamp lastModified;

    @Override
    public long getRemoteId() {
        return id;
    }

    @Override
    public Long getCategoryId() {
        return null;
    }

    @Override
    public Long getManufacturerId() {
        return null;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public double getPrice() {
        return unitPrice;
    }

    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    @Override
    public String getCategoryName() {
        return null;
    }

    @Override
    public String getManufacturerName() {
        return null;
    }

    @Override
    public String getUpc() {
        return null;
    }

    @Override
    public String getEan() {
        return null;
    }

    @Override
    public String getStoreSku() {
        return null;
    }

    @ExternalField(value="id",required=true)
    public void setId(long id) {
        this.id = id;
    }

    @ExternalField("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Inherited from invoice line
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    /**
     * Inherited from invoice
     */
    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }
}
