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
package com.sonrisa.swarm.rics.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.rics.util.RicsIdGenerator;

public class RicsInvoiceLineDTO extends InvoiceLineDTO {
	/**
	 * InvoiceLine identifier in RICS
	 */
	private Short ticketLineNumber;

	/**
	 * the invoice's id this invoiceline belongs to
	 */
	private Long invoiceId;

	/**
	 * the product's id this invoiceLine refers to 
	 */
	private Long productId;

	/**
	 * the quantity of this invoiceline (number of items described by this instance)
	 */
	private int quantity;

	/**
	 * net price the customer must pay for this invoiceLine 
	 */
	private BigDecimal amountPaid;

	/**
	 * the amount the customer must pay after this invoiceLine
	 */
	private double taxAmount;

	/**
	 * the invoiceLine's last modification time stamp  
	 */
	private Timestamp modifiedOn;
	
	/**
	 * generated id to use as ls_line_id (RICS doesn't supply unique id)
	 */
	private Long generatedId;

	/**
	 * 
	 */
	@Override
	public long getRemoteId() {
		if (generatedId == null) {
			generatedId = RicsIdGenerator.generateLineId(invoiceId.longValue(), ticketLineNumber.shortValue()); 
		}
		return generatedId;
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
		return amountPaid.doubleValue();
	}

	public BigDecimal getPriceAsBigDecimal() {
		return amountPaid;
	}

	@Override
	public double getTax() {
		return taxAmount;
	}

	@Override
	public Timestamp getLastModified() {
		return modifiedOn;
	}

	@ExternalField(value = "TicketLineNumber", required = true)
	public void setTicketLineNumber(short ticketLineNumber) {
		this.ticketLineNumber = Short.valueOf(ticketLineNumber);
	}

	// should be manually set
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	// should be manually set
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@ExternalField(value = "Quantity")
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@ExternalField(value = "AmountPaid")
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = new BigDecimal(amountPaid);
	}

	// should be manually set
	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	@ExternalField(value = "ModifiedOn")
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = new Timestamp(ISO8061DateTimeConverter.stringToDate(modifiedOn).getTime());
	}

}
