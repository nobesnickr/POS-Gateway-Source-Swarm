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
package com.sonrisa.swarm.model.staging;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Staging entity for products, representing rows in the <code>staging_products</code> table.
 *
 * @author joe
 */
@Entity
@Table(name = ProductStage.TABLE_NAME)
@StageInsertableType(dbTableName = "products", storeIdColumnName = "store_id")
public class ProductStage extends BaseStageEntity {
    
    private static final long serialVersionUID = 199945386845660732L;

    public static final String TABLE_NAME = "staging_products";
    
    private Long id;   
        
    /**
     * Inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * 
     * Store id is for any stores not from Retail Pro
     */
    private Long storeId;          

    /**
     * Each Retail Pro store is identified by three values:
     * <ul>
     *  <li>SwarmId (picked for each swarm-partner)</li>
     *  <li>Store number (e.g. NAS)</li>
     *  <li>Subsidiary (e.g. 001), for Rp9 this is optional but clients sends it anyway</li>
     * </ul>
     * 
     * These values are only used for Retail Pro stores.
     */
    @RetailProAttr
    private String swarmId; 
       
    @RetailProAttr(value = "StoreNo",  maxLength = 100)
    private String lsStoreNo;

    @RetailProAttr(value = "SbsNo", maxLength = 100)
    private String lsSbsNo;    

    /**
     * Legacy system's ID for the product
     */
    @RetailProAttr(value = "ProductSid", maxLength = 100)
    private String lsProductId;

    /**
     * Stock keeping unit, normally whatever identifier the
     * partner uses to keep track of their products, e.g. <i>IPHONE-WH-001</i>
     */
    @RetailProAttr(value = "Sku", maxLength = 50)
    private String sku;
    
    /**
     * Alternate field for {@link #sku} in case there are more.
     */
    private String storeSku;

    /**
     * Category ID in the legacy system, if category entities are 
     * represented separately from the product, otherwise use category
     */
    private String lsCategoryId;
    
    /**
     * Category name. Should only have value when {@link #lsCategoryId} is not available,
     * and individual category entities can't be created
     */
    @RetailProAttr(value = "Category", maxLength = 100)
    private String category;
	
    /**
     * Manufacturer ID in the legacy system, if category entities are 
     * represented separately from the product, otherwise use category
     */
    private String lsManufacturerId;
	
    /**
     * Manufacturer name. Should only have value when {@link #lsManufacturerId} is not available,
     * and individual manufacturer entities can't be created
     */
    private String manufacturer;

    /**
     * Universal Product Code, basically the barcode on the product
     * Its a 12 digit number.
     */
    @RetailProAttr(value = "Upc", maxLength = 14)
    private String upc;
    
    /**
     * International Article Number, a usually European alternative for the 
     * {@link #upc}. Contains 13-17 numeric digits. 
     */
    private String ean;

    /**
     * Products short description, its unique name. E.g. iPhone Black 65GB
     */
    @RetailProAttr(value = "Desc", maxLength = 250)
    private String description;

    /**
     * Product's price, net price. 
     * Warning, this field contains the products in the store's own currency,
     * as currency rates may change, while the invoices's price contains USD values.
     * TODO Fix this issue if requested, otherwise this is known bug.
     */
    @RetailProAttr(value = "Price", maxLength = 20, truncatingAllowed = false)
    private String price;

    /**
     * Last modification date of the product, used to filter only for the new products
     */
    @RetailProAttr(value = "ModifiedDate", maxLength = 20)
    private String modifiedAt;   

    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------   
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }            

    @StageInsertableAttr(dbColumnName="ls_store_no")
    @Column(name = "ls_store_no")
    public String getLsStoreNo() {
        return lsStoreNo;
    }

    public void setLsStoreNo(String lsStoreNo) {
        this.lsStoreNo = lsStoreNo;
    }

    @StageInsertableAttr(dbColumnName="ls_sbs_no")
    @Column(name = "ls_sbs_no")
    public String getLsSbsNo() {
        return lsSbsNo;
    }

    public void setLsSbsNo(String lsSbsNo) {
        this.lsSbsNo = lsSbsNo;
    }

    @StageInsertableAttr(dbColumnName="ls_product_id")
    @Column(name = "ls_product_id")
    public String getLsProductId() {
        return lsProductId;
    }

    public void setLsProductId(String lsProductId) {
        this.lsProductId = lsProductId;
    }

    @StageInsertableAttr(dbColumnName="sku")
    @Column(name = "sku")
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @StageInsertableAttr(dbColumnName="store_sku")
    @Column(name = "store_sku")
    public String getStoreSku() {
        return storeSku;
    }

    public void setStoreSku(String storeSku) {
        this.storeSku = storeSku;
    }

    @Column(name="ls_category_id")
    public String getLsCategoryId(){
    	return this.lsCategoryId;
    }
    
    public void setLsCategoryId(String lsCategoryId) {
        this.lsCategoryId = lsCategoryId;
    }

    @StageInsertableAttr(dbColumnName="category")
    @Column(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(name="ls_manufacturer_id")
    public String getLsManufacturerId(){
    	return this.lsManufacturerId;
    }
    
    public void setLsManufacturerId(String lsManufacturerId) {
        this.lsManufacturerId = lsManufacturerId;
    }

    @StageInsertableAttr(dbColumnName="manufacturer")
    @Column(name = "manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @StageInsertableAttr(dbColumnName="upc")
    @Column(name = "upc")
    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    @StageInsertableAttr(dbColumnName="ean")
    @Column(name = "ean")
    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    @StageInsertableAttr(dbColumnName="description")
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @StageInsertableAttr(dbColumnName="price")
    @Column(name = "price")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @StageInsertableAttr(dbColumnName="last_modified")
    @Column(name = "last_modified")
    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    @Override
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    public String getSwarmId() {
        return swarmId;
    }

    @Override
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }   
    
    /**
     * Returns the inner ID of the store where this staging entity comes from.
     * 
     * Inner means the ID identifies this store within the Swarm System.
     * This ID could be null if the entity does not know the inner ID of its store.
     * E.g.: Entities from RetailPro stores.
     */
    @Column(name = "store_id")
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    // ------------------------------------------------------------------------
    // ~ Object methods
    // ------------------------------------------------------------------------ 

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lsProductId == null) ? 0 : lsProductId.hashCode());
        result = prime * result + ((lsSbsNo == null) ? 0 : lsSbsNo.hashCode());
        result = prime * result + ((lsStoreNo == null) ? 0 : lsStoreNo.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((swarmId == null) ? 0 : swarmId.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductStage other = (ProductStage) obj;
        if (lsProductId == null) {
            if (other.lsProductId != null)
                return false;
        } else if (!lsProductId.equals(other.lsProductId))
            return false;
        if (lsSbsNo == null) {
            if (other.lsSbsNo != null)
                return false;
        } else if (!lsSbsNo.equals(other.lsSbsNo))
            return false;
        if (lsStoreNo == null) {
            if (other.lsStoreNo != null)
                return false;
        } else if (!lsStoreNo.equals(other.lsStoreNo))
            return false;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        } else if (!storeId.equals(other.storeId))
            return false;
        if (swarmId == null) {
            if (other.swarmId != null)
                return false;
        } else if (!swarmId.equals(other.swarmId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProductStage [id=" + id + ", storeId=" + storeId + ", swarmId=" + swarmId + ", lsStoreNo=" + lsStoreNo
                + ", lsSbsNo=" + lsSbsNo + ", lsProductId=" + lsProductId + ", sku=" + sku + ", storeSku=" + storeSku
                + ", lsCategoryId=" + lsCategoryId + ", category=" + category + ", lsManufacturerId="
                + lsManufacturerId + ", manufacturer=" + manufacturer + ", upc=" + upc + ", ean=" + ean
                + ", description=" + description + ", price=" + price + ", modifiedAt=" + modifiedAt + "]";
    }
}
