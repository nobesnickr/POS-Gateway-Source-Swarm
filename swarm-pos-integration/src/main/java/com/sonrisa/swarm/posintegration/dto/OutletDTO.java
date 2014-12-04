package com.sonrisa.swarm.posintegration.dto;

import java.sql.Timestamp;
import java.util.Date;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

@StageInsertableType(dbTableName="outlets")
public abstract class OutletDTO implements DWTransferable{
	
    /** Identifier of the outlet in the remote system */
    @StageInsertableAttr(dbColumnName = "ls_outlet_id")
    public abstract long getRemoteId();
	    
    /** Outlet's name, e.g. "Swarm" */
    @StageInsertableAttr(dbColumnName = "name")
	public abstract String getOutletName();
    
    /** Outlet's First Address */
    @StageInsertableAttr(dbColumnName = "address1")
	public abstract String getAddress1();

    /** Outlet's Second Address */
    @StageInsertableAttr(dbColumnName = "address2")
	public abstract String getAddress2();

    /** Outlet's city */
    @StageInsertableAttr(dbColumnName = "city")
	public abstract String getCity();
    
    /** Outlet's state */
    @StageInsertableAttr(dbColumnName = "state")
	public abstract String getState();
    
    /** Outlet's postcode */
    @StageInsertableAttr(dbColumnName = "postcode")
	public abstract String getPostCode();
    
    /** Outlet's country */
    @StageInsertableAttr(dbColumnName = "country")
	public abstract String getCountry();
    
    /** Outlet's contact first name */
    @StageInsertableAttr(dbColumnName = "firstname")
	public abstract String getFirstName();
    
    /** Outlet's contact last name */
    @StageInsertableAttr(dbColumnName = "lastname")
	public abstract String getSecondName();
    
    /** Outlet's contact email */
    @StageInsertableAttr(dbColumnName = "email")
	public abstract String getEmail();
    
    /** Outlet's contact phone */
    @StageInsertableAttr(dbColumnName = "phone")
	public abstract String getPhone();
    
    /** Outlet's contact email */
    @StageInsertableAttr(dbColumnName = "fax")
	public abstract String getFax();

    /** Outlet's contact email */
    @StageInsertableAttr(dbColumnName = "website")
	public abstract String getWebsite();
    
	/** Timestamp of the outlet entry in the remote system */
    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public Timestamp getLastModified(){
    	return new Timestamp(0);
	}

	@Override
	public String toString() {
		return "OutletDTO [getOutletName()=" + getOutletName() + ", getAddress1()="
				+ getAddress1() + ", getAddress2()=" + getAddress2()
				+ ", getCity()=" + getCity() + ", getState()=" + getState()
				+ ", getPostCode()=" + getPostCode() + ", getCountry()="
				+ getCountry() + ", getFirstName()=" + getFirstName()
				+ ", getSecondName()=" + getSecondName() + ", getEmail()="
				+ getEmail() + ", getPhone()=" + getPhone() + ", getFax()="
				+ getFax() + ", getWebsite()=" + getWebsite()
				+ ", getLastModified()=" + getLastModified() + "]";
	}		
}
