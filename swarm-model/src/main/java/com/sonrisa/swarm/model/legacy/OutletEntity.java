package com.sonrisa.swarm.model.legacy;

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
import javax.validation.constraints.Size;

/**
 * Outlet entity. It represents a record from the "outlets" table in the legacy DB.
 */
@Entity
@Table(name = OutletEntity.TABLE_NAME)
public class OutletEntity extends BaseLegacyEntity {

	private static final long serialVersionUID = 4871319798653183709L;

    /** Name of the DB table. */
    public static final String TABLE_NAME = "outlets";
	
    /** Private key in the staging_outlets table */
    private Long id;
       
    
    /** Store id for the instance */
    private Long outletId;    
    
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
	private Date lastModified;
	
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outlet_id")
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

	
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return new Date();
    }

    public void setLastModified(Date ts) {
        this.lastModified = ts;
    }

    @Column(name = "ls_outlet_id")
	public Long getOutletId() {
		return outletId;
	}

	public void setOutletId(Long outletId) {
		this.outletId = outletId;
	}

	@Column(name = "name")
    @Size(max=50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "address1")
    @Size(max=50)
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@Column(name = "address2")
    @Size(max=50)
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Column(name = "city")
    @Size(max=50)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state")
    @Size(max=50)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "postcode")
    @Size(max=15)
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@Column(name = "country")
    @Size(max=50)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "firstName")
    @Size(max=50)
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "lastname")
    @Size(max=50)
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "email")
    @Size(max=50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "phone")
    @Size(max=50)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "fax")
    @Size(max=50)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "website")
    @Size(max=50)
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "OutletEntity [id=" + id + ", outletId=" + outletId + ", name="
				+ name + ", address1=" + address1 + ", address2=" + address2
				+ ", city=" + city + ", state=" + state + ", postcode="
				+ postcode + ", country=" + country + ", firstname="
				+ firstname + ", lastname=" + lastname + ", email=" + email
				+ ", phone=" + phone + ", fax=" + fax + ", website=" + website
				+ ", lastModified=" + lastModified + "]";
	}
}
