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
package hu.sonrisa.backend.versionedobject;

import hu.sonrisa.backend.model.PersistenceAwareBase;
import hu.sonrisa.backend.model.util.VersionedObjectUtil;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * @author Palesz
 */
public abstract class VersionedObjectBase
        extends PersistenceAwareBase<String>
        implements VersionedObject {

    private String szuloId;
    private String verzioNev;
    private String verzioMegjegyzes;
    private String kod;
    private String createdBy = " ";
    private Date createdAt;
    /** Logika torlest jelzo flag. */
    private boolean deleted;

    /**
     * JAXB
     */
    protected VersionedObjectBase() {
        super();
    }

    /**
     * 
     * @param objectVersion
     */
    public VersionedObjectBase(Integer objectVersion) {
        super(objectVersion);
    }

    @Override
    @XmlAttribute(name = "uuid")
    public final String getId() {
        return getPersistenceID();
    }

    /**
     * 
     * @param id
     */
    @Override
    public final void setId(String id) {
        this.setPersistenceID(id);
    }

    @XmlAttribute(name = "szuloUUID")
    @Override
    public final String getSzuloId() {
        return szuloId;
    }

    /**
     * 
     * @param szuloId
     */
    @Override
    public final void setSzuloId(String szuloId) {
        this.szuloId = szuloId;
    }

    @XmlAttribute(name = "verzioNev")
    @Override
    public final String getVerzioNev() {
        return verzioNev;
    }

    /**
     * 
     * @param verzioNev
     */
    @Override
    public void setVerzioNev(String verzioNev) {        
        this.verzioNev = verzioNev;
    }

    @XmlElement(name = "verzioMegjegyzes")
    @Override
    public final String getVerzioMegjegyzes() {
        return verzioMegjegyzes;
    }

    /**
     * 
     * @param verzioMegjegyzes
     */
    @Override
    public void setVerzioMegjegyzes(String verzioMegjegyzes) {
        this.verzioMegjegyzes = verzioMegjegyzes;
    }

    @XmlAttribute(name = "kod")
    @Override
    public String getKod() {
        return kod;
    }

    /**
     * 
     * @param kod
     */
    @Override
    public final void setKod(String kod) {
        this.kod = kod;
    }

    /**
     * 
     * @return
     */
    @XmlAttribute(name = "createdBy")
    @Override
    public final String getCreatedBy() {
        return createdBy;
    }

    /**
     * 
     * @param createdBy
     */
    @Override
    public final void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    @Override
    public final String getModifiedBy() {
        return getCreatedBy();
    }

    /**
     * 
     * @param modifiedBy
     */
    @Override
    public final void setModifiedBy(String modifiedBy) {
        this.setCreatedBy(modifiedBy);
    }

    /**
     * 
     * @return
     */
    @XmlAttribute(name = "createdAt")
    @Override
    public final Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     */
    @Override
    public final void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    @Override
    public final Date getModifiedAt() {
        return getCreatedAt();
    }

    /**
     * 
     * @param modifiedAt
     */
    @Override
    public final void setModifiedAt(Date modifiedAt) {
        this.setCreatedAt(modifiedAt);
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    public final String getMegjelenithetoVerzio() {
        return VersionedObjectUtil.getMegjelenithetoVerzio(this);
    }

    @XmlAttribute(name = "del")
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    
}
