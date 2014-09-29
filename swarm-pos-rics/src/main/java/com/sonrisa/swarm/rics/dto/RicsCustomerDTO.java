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

import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

public class RicsCustomerDTO extends CustomerDTO {
	/**
	 * customer identifier in RICS
	 */
	private long accountNumber;
	private Timestamp modifiedOn;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String city;
	private String postalCode;
	private String state;

	public RicsCustomerDTO() {
	}

	@Override
	public long getRemoteId() {
		return accountNumber;
	}

	@Override
	public Timestamp getLastModified() {
		return modifiedOn;
	}

	@Override
	public String getName() {
		return firstName.concat(" ").concat(lastName);
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPhoneNumber() {
		return null;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getAddress2() {
		return null;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getPostalCode() {
		return postalCode;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public String getCountry() {
		return null;
	}

	@Override
	public String getNotes() {
		return null;
	}

	@ExternalField(value = "AccountNumber")
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	@ExternalField(value = "ModifiedOn")
	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	@ExternalField(value = "FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@ExternalField(value = "LastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ExternalField(value = "Email")
	public void setEmail(String email) {
		this.email = email;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setState(String state) {
		this.state = state;
	}

}
