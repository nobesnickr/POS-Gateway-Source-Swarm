package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;
import java.text.ParseException;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.vend.api.util.TimeConversionUtil;

/**
 * Representation of a Vend product
 */
public class VendProductDTO extends ProductDTO {

    /**
     * Foreign id
     */
    private long id;
    
    /**
     * String uuid 
     */
    private String uuidProductId;
    
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
    
    /**
     * Category's name
     */
    private String categoryName;

    /**
     * Manufacturer's name
     */
    private String manufacturer;
    
    /**
     * Sku
     */
    private String sku;
    
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
        return categoryName;
    }

    @Override
    public String getManufacturerName() {
        return manufacturer;
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

    
    public void setId(long id) {
        this.id = id;
    }

    @ExternalField("description")
    public void setName(String name) {
        this.name = name;
    }

    @ExternalField("price")
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    @ExternalField("brand_name")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @ExternalField("updated_at")
    public void setLastModified(String lastModified) throws ParseException {
        this.lastModified = TimeConversionUtil.stringToDate(lastModified);
    }

    @ExternalField("supplier_name")
    public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
    
	public String getUuidProductId() {
		return uuidProductId;
	}

	@ExternalField(value="id",required=true)
	public void setUuidProductId(String uuidProductId) {
		this.uuidProductId = uuidProductId;
	}

	@ExternalField(value="sku")
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	@Override
	public String getSku() {
		return sku;
	}
}
