/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Azon objektumok őse, amik XML root level tag-ek lehetnek XML dokumentumokban
 * 
 * @author cserepj
 */
public class XMLObject extends Object implements Serializable {

    private Integer objectVersion = 0;

    /**
     * jaxb részére
     *
     */
    public XMLObject() {
    }

    /**
     * 
     * @param objectVersion
     */
    public XMLObject(Integer objectVersion) {
        this.objectVersion = objectVersion;
    }

    /**
     * Objektum verzió
     * 
     * @return 
     */
    @XmlAttribute
    public Integer getObjectVersion() {
        return objectVersion;
    }
}
