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
package com.sonrisa.swarm.model.legacy;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 *
 * @author joe
 */
@Entity
@Table(name = ProductEntity.TABLE_NAME)
public class ProductEntity extends BaseLegacyEntity {
    
    /** Name of the DB table. */
    public static final String TABLE_NAME = "products";

    private static final long serialVersionUID = 737145789222623443L;
    
    private Long id;
    private String sku;
    private String category;
    private String manufacturer;
    private String upc;
    private String ean;
    private String description;
    private BigDecimal price;
    private Date lastModified;

    @Override
    public String toString() {
        return "ProductEntity{" + "id=" + id + ", lsProductId=" + getLegacySystemId() 
                + ", store=" + getStore() + ", sku=" + sku + ", category=" + category 
                + ", manufacturer=" + manufacturer + ", upc=" + upc + ", ean=" + ean 
                + ", description=" + description + ", price=" + price + ", lastModified=" + lastModified + '}';
    }
    
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @Override
    public Long getId() {
       return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ls_product_id")
    public Long getLsProductId() {
        return getLegacySystemId();
    }

    public void setLsProductId(Long lsProductId) {
        setLegacySystemId(lsProductId);
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }
    @Column(name = "sku")
    @Size(max=50)
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Column(name = "category")
    @Size(max=100)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(name = "manufacturer")
    @Size(max=50)
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Column(name = "upc")
    @Size(max=14)
    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    @Column(name = "ean")
    @Size(max=14)
    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    @Column(name = "description")
    @Size(max=250)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "price")
    @Min(-999999)
    @Max(999999)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)    
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
