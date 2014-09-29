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

package com.sonrisa.swarm.lspro.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * DTO for products from Lightspeed Pro
 */
public class LsProProductDTO extends ProductDTO {

    private Long id;
    
    private Long dateModified;
    
    private String sku;
    
    private String shortDescription;
    
    private String description;
    
    private String className;
    
    private String familyName;
    
    private BigDecimal sellPrice;
    
    @Override
    public long getRemoteId() {
        return this.id;
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
        if(StringUtils.hasLength(this.shortDescription)){
            return this.shortDescription;
        } else if(StringUtils.hasLength(this.description)){
            return this.description;
        } else {
            return null;
        }
    }

    @Override
    public double getPrice() {
        return this.sellPrice.doubleValue();
    }

    @Override
    public Timestamp getLastModified() {
        return new Timestamp(this.dateModified);
    }

    @Override
    public String getCategoryName() {
        return this.className;
    }

    @Override
    public String getManufacturerName() {
        return this.familyName;
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
        return this.sku;
    }
    
    @ExternalField(value = "Id", required = true)
    public void setId(Long id) {
        this.id = id;
    }

    @ExternalField(value = "DateModified")
    public void setDateModified(String dateModified) {
        this.dateModified = ISO8061DateTimeConverter.stringToDate(dateModified).getTime();
    }

    @ExternalField(value = "SKU")
    public void setSku(String sku) {
        this.sku = sku;
    }

    @ExternalField(value = "ShortDescription")
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    @ExternalField(value = "Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @ExternalField(value = "ClassName")
    public void setClassName(String className) {
        this.className = className;
    }
    
    @ExternalField(value = "Family")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @ExternalField(value = "SellPrice")
    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }
}
