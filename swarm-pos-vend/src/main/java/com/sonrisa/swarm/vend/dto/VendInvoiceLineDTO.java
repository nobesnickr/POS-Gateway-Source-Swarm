package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Vend order item
 */
public class VendInvoiceLineDTO extends InvoiceLineDTO {
	/**
     * Invoice's ID
     */
    private Long invoiceId;
    
    /**
     * Invoice's ID as a uuid
     */
    private String uuidInvoiceId;
    
    /**
     * Line number inside the invoice
     */
    private long lineNumber;
    
    /**
     * Line number as a uuid inside the invoice
     */
    private String stringLineNumber;
    
    /**
     * Product relating to this invoice
     */
    private Long productId;
    
    /**
     * Product's ID as a uuid
     */
    private String uuidProductId;

	/**
     * Price per unit
     */
    private double unitPrice = 0.0D;
    
    /**
     * Tax per unit
     */
    private double unitTax = 0.0D;
    
    /**
     * Quantity of the product purchased
     */
    private int quantity = 0;
    
    private String name;
    
    private Timestamp ts;

    @Override
    public long getRemoteId() {
        return lineNumber;
    }

    @Override
    public Long getInvoiceId() {
        return invoiceId;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getPrice() {
        return unitPrice;
    }

    @Override
    public double getTax() {
        return unitTax;
    }


    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Timestamp getLastModified(){
    	return ts;
    }
    
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @ExternalField(value = "id", required = true)
    public void setStringLineNumber(String lineNumber) {
        this.stringLineNumber = lineNumber;
    }    

	public String getStringLineNumber() {
		return stringLineNumber;
	}
    
    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @ExternalField("price_total")
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @ExternalField("tax")
    public void setUnitTax(double unitTax) {
        this.unitTax = unitTax;
    }

    @ExternalField("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
	public String getUuidInvoiceId() {
		return uuidInvoiceId;
	}
	
	@ExternalField("invoiceId")
	public void setUuidInvoiceId(String uuidInvoiceId) {
		this.uuidInvoiceId = uuidInvoiceId;
	}
	
    public String getUuidProductId() {
		return uuidProductId;
	}

    @ExternalField("product_id")
    public void setUuidProductId(String uuidProductId){
		this.uuidProductId = uuidProductId;
	}
    
    @ExternalField("name")
    public void setName(String name){
		this.name = name;
	}
    
    @ExternalField("sale_date")
    public void setTimestamp(Timestamp ts){
		this.ts = ts;
	}
}
