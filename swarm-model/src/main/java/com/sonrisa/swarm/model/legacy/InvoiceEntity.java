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

import com.sonrisa.swarm.model.constraint.AfterDate;

/**
 * Invoice entity.
 *
 * @author Béla Szabó
 */
@Entity
@Table(name = "invoices")
public class InvoiceEntity extends BaseLegacyEntity {

    private static final long serialVersionUID = -6247107871024226208L;
    
    private Long id;    
    private Long lsCustomerId;    
    private String invoiceNo;  
    private Date ts;
    private BigDecimal total;
    private Boolean completed;
    private Boolean linesProcessed;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Date lastModified;
    private OutletEntity outlet;
    private Long lsOutletId;
    private RegisterEntity register;
    private Long lsRegisterId;

    @Override
    public String toString() {
        return "InvoiceEntity [id=" + id 
                + ", lsInvoiceId=" + getLegacySystemId() + ", lsCustomerId=" + lsCustomerId
                + ", invoiceNo=" + invoiceNo + ", ts=" + ts 
                + ", total=" + total + ", completed=" + completed
                + ", linesProcessed=" + linesProcessed + ", store=" + getStore() + "]";
    }

    // ------------------------------------------------------------------------
    // ~ Setters / getters
    // ------------------------------------------------------------------------
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    @Override    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }
    
    /**
     * Even though the column is named <code>customer_id</code>,
     * it is actually a legacy id, and the value from remote
     * system should be written here, matching <code>customers.ls_customer_id</code>     
     */
    @Column(name = "customer_id")
    public Long getLsCustomerId() {
      return lsCustomerId;
    }

    public void setLsCustomerId(Long lsCustomerId) {
      this.lsCustomerId = lsCustomerId;
    }
    
    @Column(name = "ts")
    @Temporal(TemporalType.TIMESTAMP)
    @AfterDate("2000-01-01")
    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    
    @Column(name = "total")
    @NotNull // When String to BigDecimal fails at staging conversion these values are passed as null
    @Min(-999999)
    @Max(999999)
    public BigDecimal getTotal() {    
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Column(name = "completed")
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Column(name = "ls_invoice_id")
    public Long getLsInvoiceId() {
        return getLegacySystemId();
    }

    public void setLsInvoiceId(Long lsInvoiceId) {
        setLegacySystemId(lsInvoiceId);
    }

    @Column(name = "invoice_no")
    @Size(max=50)
    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
   

    @Column(name = "lines_processed")
    public Boolean getLinesProcessed() {
        return linesProcessed;
    }

    public void setLinesProcessed(Boolean linesProcessed) {
        this.linesProcessed = linesProcessed;
    }

    @Column(name = "name")
    @Size(max=100)
    public String getCustomerName() {
      return customerName;
    }

    public void setCustomerName(String customerName) {
      this.customerName = customerName;
    }

    @Column(name = "mainphone")
    @Size(max=50)
    public String getCustomerPhone() {
      return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
      this.customerPhone = customerPhone;
    }

    @Column(name = "email")
    @Size(max=100)
    public String getCustomerEmail() {
      return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
      this.customerEmail = customerEmail;
    }
    
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date ts) {
        this.lastModified = ts;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id")
    public OutletEntity getOutlet() {
		return outlet;
	}

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "register_id")
	public RegisterEntity getRegister() {
		return register;
	}

	public void setOutlet(OutletEntity outlet) {
		this.outlet = outlet;
	}

	public void setRegister(RegisterEntity register) {
		this.register = register;
	}

	@Column(name = "ls_outlet_id")
	public Long getLsOutletId() {
		return lsOutletId;
	}

    @Column(name = "ls_register_id")
	public Long getLsRegisterId() {
		return lsRegisterId;
	}

	public void setLsOutletId(Long lsOutletId) {
		this.lsOutletId = lsOutletId;
	}

	public void setLsRegisterId(Long lsRegisterId) {
		this.lsRegisterId = lsRegisterId;
	}
}
