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

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Product information supplied by invoice data
 * @author Sonrisa
 *
 */
public class RicsProductDTO extends ProductDTO{
	private long upc;
	private String sku;
	private String summary;

	public RicsProductDTO() {
	}


	@Override
	public long getRemoteId() {
		return upc;
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
		return summary;
	}


	@Override
	public double getPrice() {
		return 0;
	}


	@Override
	public Timestamp getLastModified() {
		return null;
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
		return upc+"";
	}


	@Override
	public String getEan() {
		return null;
	}


	@Override
	public String getStoreSku() {
		return sku;
	}

	@ExternalField(value = "UPC")
	public void setUpc(long upc) {
		this.upc = upc;
	}

	@ExternalField(value = "Sku")
	public void setSku(String sku) {
		this.sku = sku;
	}

	@ExternalField(value = "Summary")
	public void setSummary(String summary) {
		this.summary = summary;
	}
}
