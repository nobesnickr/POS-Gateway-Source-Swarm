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
package com.sonrisa.swarm.mos.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/** 
 * Manufacturer from the Merchant OS remote POS system
 */
public class MosManufacturerDTO extends ManufacturerDTO {

    /**
     * Primary key in the remote system
     */
    private long manufacturerId;
    
    /**
     *  The name/description of the manufacturer.
     */
    private String manufacturerName;
    
    /**
     * Date/time the record was last modified
     */
    private Timestamp lastModified;

    /**
     * @return the manufacturerId
     */
    @Override
    public long getRemoteId() {
        return manufacturerId;
    }

    /**
     * @return the manufacturerName
     */
    @Override
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * @return the lastModified
     */
    @Override
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @param manufacturerId the manufacturerId to set
     */
    @ExternalField(value = "manufacturerID", required = true)
    public void setManufacturerId(long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    /**
     * @param manufacturerName the manufacturerName to set
     */
    @ExternalField("manufacturerName")
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * @param lastModified the lastModified to set
     */
    @ExternalField("timeStamp")
    public void setLastModified(String lastModified) {
        this.lastModified = new Timestamp(ISO8061DateTimeConverter.stringToDate(lastModified).getTime());
    }
}
