package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.OutletDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;

/**
 * Representation of a Vend outlet's batch item
 */
public class VendOutletDTO extends OutletDTO{

	private Long remoteId;
	private String uuidOutletId;
	private String outletName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postCode;
	private String country;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String fax;
	private String website;
	
	
	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}

	public String getUuidOutletId() {
		return uuidOutletId;
	}

	@ExternalField(value = "id", required = true)
	public void setUuidOutletId(String uuidOutletId) {
		this.uuidOutletId = uuidOutletId;
	}

	@ExternalField(value = "name")
	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	@ExternalField(value = "physical_address1")
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@ExternalField(value = "physical_address2")
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@ExternalField(value = "physical_city")
	public void setCity(String city) {
		this.city = city;
	}

	@ExternalField(value = "physical_state")
	public void setState(String state) {
		this.state = state;
	}

	@ExternalField(value = "physical_postcode")
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	@ExternalField(value = "physical_country_id")
	public void setCountry(String country) {
		this.country = country;
	}

	@ExternalField(value = "contact")
	public void setFirstName(ExternalDTO dto) {
		this.firstName = getFieldFromInnerDTO(dto, "first_name");
	}

	@ExternalField(value = "contact")
	public void setLastName(ExternalDTO dto) {
		this.lastName = getFieldFromInnerDTO(dto, "last_name");
	}
	
	@ExternalField(value = "contact")
	public void setEmail(ExternalDTO dto) {
		this.email = getFieldFromInnerDTO(dto, "email");
	}

	@ExternalField(value = "contact")
	public void setPhone(ExternalDTO dto) {
		this.phone = getFieldFromInnerDTO(dto, "phone");
	}

	@ExternalField(value = "contact")
	public void setFax(ExternalDTO dto) {
		this.fax = getFieldFromInnerDTO(dto, "fax");
	}

	@ExternalField(value = "contact")
	public void setWebsite(ExternalDTO dto) {
		this.website = getFieldFromInnerDTO(dto, "website");
	}

	@Override
	public long getRemoteId() {
		return remoteId;
	}

	@Override
	public String getOutletName() {
		return outletName;
	}

	@Override
	public String getAddress1() {
		return address1;
	}

	@Override
	public String getAddress2() {
		return address2;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public String getPostCode() {
		return postCode;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getSecondName() {
		return lastName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public String getFax() {
		return fax;
	}

	@Override
	public String getWebsite() {
		return website;
	}

	@Override
	public Timestamp getLastModified() {
		return new Timestamp(0);
	}
	
	private String getFieldFromInnerDTO(ExternalDTO dto, String field){
		String fieldValue = null;
		if(dto != null){
			fieldValue = dto.getText(field);
		}
		
		fieldValue = ("null".equalsIgnoreCase(fieldValue))?null:fieldValue;
		
		return fieldValue;
	}

	@Override
	public String toString() {
		return "VendOutletDTO [remoteId=" + remoteId + ", uuidOutletId="
				+ uuidOutletId + ", outletName=" + outletName + ", address1="
				+ address1 + ", address2=" + address2 + ", city=" + city
				+ ", state=" + state + ", postCode=" + postCode + ", country="
				+ country + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", phone=" + phone + ", fax="
				+ fax + ", website=" + website + "]";
	}
}
