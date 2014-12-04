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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * InvoiceLine entity.
 *
 * @author Béla Szabó
 *
 */
@Entity
@Table(name = "invoice_lines")
public class InvoiceLineEntity extends BaseLegacyEntity {

    private static final long serialVersionUID = -3286209536575709810L;
    private Long id;
    private InvoiceEntity invoice;
    private Long lsProductId;
    private String code;
    /** Description of the product this line is refers to. */
    private String description;
    /** Category of the product this line is refers to. */
    private String clazz;
    /** Manufacturer of the product this line is refers to. */
    private String family;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal tax;
    private Date ts;

    @Override
    public String toString() {
        return "InvoiceLineEntity{" + "id=" + id + ", invoice=" + invoice 
                + ", lsLineId=" + getLegacySystemId() + ", lsProductId=" + lsProductId 
                + ", code=" + code + ", description=" + description + ", clazz=" + clazz 
                + ", family=" + family + ", quantity=" + quantity + ", price=" + price
                + ", tax=" + tax + ", ts=" + ts + ", store=" + getStore() + '}';
    }

    // ------------------------------------------------------------------------
    // ~ Getters / setters 
    // ------------------------------------------------------------------------
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_line_id")
    @Override        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    public InvoiceEntity getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceEntity invoice) {
        this.invoice = invoice;
    }

    @Column(name = "ls_line_id")
    public Long getLsLineId() {
        return getLegacySystemId();
    }

    public void setLsLineId(Long lsLineId) {
        setLegacySystemId(lsLineId);
    }

    @Column(name = "ls_product_id")
    public Long getLsProductId() {
        return lsProductId;
    }

    public void setLsProductId(Long lsProductId) {
        this.lsProductId = lsProductId;
    }

    @Column(name = "code")
    @Size(max=100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Description of the product this line is refers to.
     * 
     * @return 
     */
    @Column(name = "description")
    @Size(max=255)
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the product this line is refers to.
     * 
     * @return 
     */    
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Category of the product this line is refers to.
     * 
     * @return 
     */
    @Column(name = "class")
    @Size(max=100)
    public String getClazz() {
        return clazz;
    }

    /**
     * Set the category of the product this line is refers to.
     * 
     * @param clazz 
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     *  Manufactuter of the product this line is refers to. 
     * 
     * @return 
     */
    @Column(name = "family")
    @Size(max=100)
    public String getFamily() {
        return family;
    }

    /**
     *  Set the manufactuter of the product this line is refers to. 
     * 
     * @param family 
     */
    public void setFamily(String family) {
        this.family = family;
    }

    @Column(name = "quantity")
    @NotNull
    @Min(-32767)
    @Max(32767) // smallint(5)
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Column(name = "price")
    @NotNull // When String to BigDecimal fails at staging conversion these values are passed as null
    @Min(-999999)
    @Max(999999)
    public BigDecimal getPrice() {
        return price;
    }
    
    // If price fails to be parsed as BigDecimal, it is passed to the staging as null, 
    // the entity validation still shouldn't fail
    @Column(name = "tax")
    @Min(-999999)
    @Max(999999)
    public BigDecimal getTax() {
        return tax;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    @Column(name = "ts")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }}
