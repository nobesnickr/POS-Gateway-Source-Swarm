package com.sonrisa.swarm.model.staging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

/**
 * This entity represents a record in the staging_outlets table.
 */
@Entity
@Table(name = "staging_outlets")
@StageInsertableType(dbTableName = "outlets", storeIdColumnName = "store_id")
public class OutletStage extends BaseStageEntity{
	private static final long serialVersionUID = 1060939284262655024L;

    /** Private key in the staging_outlets table */
    private Long id;
    
    /** Swarm id of the instance */
    private String swarmId;
    
    /** Store id for the instance */
    private Long storeId;    
    
    /** Store id for the instance */
    private Long lsOutletId;    
    
    private String name;
    private String address1;    
    private String address2;    
    private String city;    
    private String state;    
    private String postcode;    
    private String country;   
    private String firstname;   
    private String lastname;    
    private String email;    
    private String phone;    
    private String fax;    
    private String website;    
    private String uuid;
	private String lastModified;
    
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    @Override
	public String getSwarmId() {
		return swarmId;
	}

	@Override
	public void setSwarmId(String swarmId) {
		this.swarmId = swarmId;
	}

	@StageInsertableAttr(dbColumnName="store_id")
    @Column(name = "store_id")
    @Override
	public Long getStoreId() {
		return this.storeId;
	}

	@StageInsertableAttr(dbColumnName="ls_outlet_id")
    @Column(name = "ls_outlet_id")
	public Long getLsOutletId() {
		return this.lsOutletId;
	}
		
	@Override
	public String getLsStoreNo() {
		return null;
	}

	@Override
	public String getLsSbsNo() {
		return null;
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return this.id;
    }

    @StageInsertableAttr(dbColumnName="name")
    @Column(name = "name")   
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @StageInsertableAttr(dbColumnName="address1")
    @Column(name = "address1")  
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@StageInsertableAttr(dbColumnName="address2")
    @Column(name = "address2")  
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@StageInsertableAttr(dbColumnName="city")
    @Column(name = "city")  
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@StageInsertableAttr(dbColumnName="state")
    @Column(name = "state")  
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@StageInsertableAttr(dbColumnName="postcode")
    @Column(name = "postcode")  
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@StageInsertableAttr(dbColumnName="country")
    @Column(name = "country")  
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@StageInsertableAttr(dbColumnName="firstname")
    @Column(name = "firstname") 
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@StageInsertableAttr(dbColumnName="lastname")
    @Column(name = "lastname")
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@StageInsertableAttr(dbColumnName="email")
    @Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@StageInsertableAttr(dbColumnName="phone")
    @Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@StageInsertableAttr(dbColumnName="fax")
    @Column(name = "fax")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@StageInsertableAttr(dbColumnName="website")
    @Column(name = "website")
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@StageInsertableAttr(dbColumnName="uuid")
    @Column(name = "uuid")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public void setLsOutletId(Long outletId) {
		this.lsOutletId = outletId;
	}
	
	@StageInsertableAttr(dbColumnName="last_modified")
    @Column(name = "last_modified")
    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result
				+ ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fax == null) ? 0 : fax.hashCode());
		result = prime * result
				+ ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((lsOutletId == null) ? 0 : lsOutletId.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result
				+ ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
		result = prime * result + ((swarmId == null) ? 0 : swarmId.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutletStage other = (OutletStage) obj;
		if (address1 == null) {
			if (other.address1 != null)
				return false;
		} else if (!address1.equals(other.address1))
			return false;
		if (address2 == null) {
			if (other.address2 != null)
				return false;
		} else if (!address2.equals(other.address2))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fax == null) {
			if (other.fax != null)
				return false;
		} else if (!fax.equals(other.fax))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (lsOutletId == null) {
			if (other.lsOutletId != null)
				return false;
		} else if (!lsOutletId.equals(other.lsOutletId))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (postcode == null) {
			if (other.postcode != null)
				return false;
		} else if (!postcode.equals(other.postcode))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (storeId == null) {
			if (other.storeId != null)
				return false;
		} else if (!storeId.equals(other.storeId))
			return false;
		if (swarmId == null) {
			if (other.swarmId != null)
				return false;
		} else if (!swarmId.equals(other.swarmId))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutletStage [id=" + id + ", swarmId=" + swarmId + ", storeId="
				+ storeId + ", outletId=" + lsOutletId + ", name=" + name
				+ ", address1=" + address1 + ", address2=" + address2
				+ ", city=" + city + ", state=" + state + ", postcode="
				+ postcode + ", country=" + country + ", firstname="
				+ firstname + ", lastname=" + lastname + ", email=" + email
				+ ", phone=" + phone + ", fax=" + fax + ", website=" + website
				+ ", uuid=" + uuid + "]";
	}

}
