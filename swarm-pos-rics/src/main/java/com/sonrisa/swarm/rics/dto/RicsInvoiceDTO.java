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

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

public class RicsInvoiceDTO extends InvoiceDTO {

	/** Id shown on ticket */
	private Long ticketNumber;

	/** Date/Time invoice was last modified in POS system - local time */
	private Timestamp ticketModifiedOn;

	/** Date/Time invoice was created in POS system - local time */
	private Timestamp ticketDateTime;

	private BigDecimal total;

	private Long customerId;

	@ExternalField(value = "TicketNumber", required = true)
	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	@ExternalField(value = "TicketModifiedOn")
	public void setTicketModifiedOn(Timestamp ticketModifiedOn) {
		this.ticketModifiedOn = ticketModifiedOn;
	}

	@ExternalField(value = "TicketDateTime")
	public void setTicketDateTime(String ticketDateTime) {
		this.ticketDateTime = new Timestamp(ISO8061DateTimeConverter.stringToDate(ticketDateTime).getTime());
	}

	@ExternalField(value = "AccountNumber")
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	@Override
	public long getRemoteId() {
		return ticketNumber;
	}

	@Override
	public Long getCustomerId() {
		return customerId;
	}

	@Override
	public String getInvoiceNumber() {
		return ticketNumber != null ? ticketNumber.toString() : null;
	}

	@Override
	public double getTotal() {
		return total.doubleValue();
	}

	@Override
	public Timestamp getLastModified() {
		return ticketModifiedOn;
	}

	@Override
	public Timestamp getInvoiceTimestamp() {
		return ticketDateTime;
	}

}
