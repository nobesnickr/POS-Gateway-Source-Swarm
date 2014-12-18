package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.RegisterDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Vend register's batch item
 */
public class VendRegisterDTO extends RegisterDTO{
	
	private Long remoteId;
	private String uuidregisterId;
	private String name;
	private String uuidOutletId;
	private Long outletId;
	
	@Override
	public long getRemoteId() {
		return remoteId;
	}


	public void setOutletId(Long outletId) {
		this.outletId = outletId;
	}

	@Override
	public Long getOutletId() {
		return outletId;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getUuidregisterId() {
		return uuidregisterId;
	}

	@ExternalField(value = "id", required = true)
	public void setUuidregisterId(String uuidregisterId) {
		this.uuidregisterId = uuidregisterId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}

	@ExternalField(value = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getUuidOutletId() {
		return uuidOutletId;
	}

	@ExternalField(value = "outlet_id", required = true)
	public void setUuidOutletId(String uuidOutletId) {
		this.uuidOutletId = uuidOutletId;
	}

	@Override
	public Timestamp getLastModified() {
		return new Timestamp(0);
	}


	@Override
	public String toString() {
		return "VendRegisterDTO [remoteId=" + remoteId + ", uuidregisterId="
				+ uuidregisterId + ", name=" + name + ", uuidOutletId="
				+ uuidOutletId + ", outletId=" + outletId + "]";
	}
	
	

}
