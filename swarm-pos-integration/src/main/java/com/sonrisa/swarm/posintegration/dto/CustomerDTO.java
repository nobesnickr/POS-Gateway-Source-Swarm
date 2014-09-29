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
package com.sonrisa.swarm.posintegration.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Customer might be real life person our a company who has made a purchase
 * at one of Swarm's partner. Customers are present on Invoices.
 */
@StageInsertableType(dbTableName = "customers")
public abstract class CustomerDTO implements DWTransferable {
	
    @StageInsertableAttr(dbColumnName = "ls_customer_id", usedAsRemoteId = true)
	public abstract long getRemoteId();

    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)    
	public abstract Timestamp getLastModified();

    @StageInsertableAttr(dbColumnName = "name", maxLength = 100)
	public abstract String getName();

    @StageInsertableAttr(dbColumnName = "firstName", maxLength = 50)
	public abstract String getFirstName();

    @StageInsertableAttr(dbColumnName = "lastName", maxLength = 50)
	public abstract String getLastName();

    @StageInsertableAttr(dbColumnName = "email", maxLength = 100)
	public abstract String getEmail();

    @StageInsertableAttr(dbColumnName = "phone", maxLength = 20)
	public abstract String getPhoneNumber();

    @StageInsertableAttr(dbColumnName = "address1", maxLength = 100)
	public abstract String getAddress();

    @StageInsertableAttr(dbColumnName = "address2", maxLength = 100)
	public abstract String getAddress2();

    @StageInsertableAttr(dbColumnName = "city", maxLength = 50)
	public abstract String getCity();

	public abstract String getPostalCode();

    /** The state of the customer, only used for US, CA, MX, AU */
    @StageInsertableAttr(dbColumnName = "state", maxLength = 2)
	public abstract String getState();

	public abstract String getCountry();
	
    @StageInsertableAttr(dbColumnName = "notes", maxLength = 0)
	public abstract String getNotes();

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomerDTO [getCustomerId()=" + getRemoteId()
                + ", getLastModified()=" + getLastModified() + ", getName()="
                + getName() + ", getEmail()=" + getEmail()
                + ", getPhoneNumber()=" + getPhoneNumber() + ", getAddress()="
                + getAddress() + ", getAddress2()=" + getAddress2()
                + ", getCity()=" + getCity() + ", getPostalCode()="
                + getPostalCode() + ", getState()=" + getState()
                + ", getCountry()=" + getCountry() + "]";
    }
}
