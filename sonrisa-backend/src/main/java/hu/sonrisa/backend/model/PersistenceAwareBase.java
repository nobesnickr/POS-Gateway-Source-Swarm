/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Sonrisa Informatikai Kft. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sonrisa.
 *
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @param <T> 
 * @author dobyman
 */
@XmlTransient
public class PersistenceAwareBase<T extends Serializable> extends XMLObject implements PersistenceAware<T> {

    /**
     * Itt tartjuk a perzisztens objektum azonositojat. A dao reteg felelos azert, hogy ezt beallitsa.
     * @return
     */
    private T persistenceID;

    /**
     * wrapper entity version erteke, csak a K11WrapperEntity.onUnWrap allithatja az erteket.
     */
    private Long JPAVersion;

    /**
     * JAXB
     */
    protected PersistenceAwareBase() {
    }

    /**
     * 
     * @param objectVersion
     */
    public PersistenceAwareBase(Integer objectVersion) {
        super(objectVersion);
    }

    @XmlTransient
    @Override
    public T getPersistenceID() {
        return persistenceID;
    }

    @Override
    public void setPersistenceID(T persistenceID) {
        this.persistenceID = persistenceID;
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    public Long getJPAVersion() {
        return JPAVersion;
    }

    /**
     * 
     * @param JPAVersion
     */
    public void setJPAVersion(Long JPAVersion) {
        this.JPAVersion = JPAVersion;
    }

    // ---------------------------------------------------
    // ~ Standard overrides
    // ---------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersistenceAwareBase other = (PersistenceAwareBase) obj;
        if (this.persistenceID != other.persistenceID && (this.persistenceID == null || !this.persistenceID.equals(other.persistenceID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.persistenceID != null ? this.persistenceID.hashCode() : 0);
        return hash;
    }
    
}
