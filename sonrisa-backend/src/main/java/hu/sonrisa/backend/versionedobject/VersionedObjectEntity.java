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
package hu.sonrisa.backend.versionedobject;

import hu.sonrisa.backend.entity.XmlWrappedEntity;
import hu.sonrisa.backend.model.FingerPrinted;
import hu.sonrisa.backend.model.PersistenceAware;
import hu.sonrisa.backend.model.PersistenceAware;
import hu.sonrisa.backend.versionedobject.VersionedObject;
import hu.sonrisa.backend.model.util.VersionedObjectUtil;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;

import javax.xml.bind.annotation.XmlTransient;


/**
 * @param <T> 
 * @author Palesz
 */
@Entity
@Table(name = "VERZIOZOTT_OBJEKTUM")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPUS", discriminatorType = DiscriminatorType.STRING)
public abstract class VersionedObjectEntity<T extends PersistenceAware<String> & VersionedObject>
        extends XmlWrappedEntity<String, T>
        implements VersionedObject, PersistenceAware<String> {

    private static final long serialVersionUID = 1L;
    /** Azonositó */
    private String id;
    /**
     * A szülő objektumverzió azonosítója.
     */
    private String szulo;
    /**
     * A objektumot a típuson belül azonosító kód.
     */
    private String kod;
    /**
     * A {@link FingerPrinted} interface-hez tartozó memberek.
     */
    private String createdBy = " ";
    /**
     * A {@link FingerPrinted} interface-hez tartozó memberek.
     */
    private Date createdAt;
    private boolean deleted = false;
    /**
     * Az objektum adott verziójának elnevezése.
     */
    private String verzioNev = " ";
    /**
     * Az objektum adott verziójának megjegyzése.
     */
    private String verzioMegjegyzes;
    private String xml;
    private Integer xmlVersion;

    private Long version;
    private String megnevezes;
    /**
     * 
     */
    public VersionedObjectEntity() {
        super(null);
    }

    /**
     * 
     * @param clazz
     */
    public VersionedObjectEntity(Class<T> clazz) {
        super(clazz);
    }

    // ------------------------------------------------------------------------
    // ~ Lifecycle methods
    // ------------------------------------------------------------------------
    @Override
    public void onWrap(T obj) {
        super.onWrap(obj);
        setKod(obj.getKod());
        setVerzioNev(obj.getVerzioNev());
        setVerzioMegjegyzes(obj.getVerzioMegjegyzes());
        setSzuloId(obj.getSzuloId());
        setId(obj.getId());
        setDeleted(obj.isDeleted());
    }

    @Override
    public void onUnWrap(T obj) {
        super.onUnWrap(obj);
        obj.setKod(getKod());
        obj.setVerzioNev(getVerzioNev());
        obj.setVerzioMegjegyzes(getVerzioMegjegyzes());
        obj.setSzuloId(getSzuloId());
        obj.setId(getId());
        obj.setDeleted(isDeleted());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionedObjectEntity<T> other = (VersionedObjectEntity<T>) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------
    @Id
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    @Basic(optional = false)
    @Column(name = "MEGNEVEZES", unique = true)
    public String getMegnevezes() {
        return megnevezes;
    }

    @Override
    public void setMegnevezes(String megnevezes) {
        this.megnevezes = megnevezes;
    }

    @Override
    @Version
    public Long getVersion() {
        return version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }

    @Basic
    @Column(name = "SZULO")
    @Override
    public String getSzuloId() {
        return szulo;
    }

    /**
     * 
     * @param szulo
     */
    @Override
    public void setSzuloId(String szulo) {
        this.szulo = szulo;
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    public String getSzuloStr() {
        return szulo == null ? "-" : szulo;
    }

    /**
     * 
     * @return
     */
    @Basic
    @Column(name = "DELETED", nullable = false)
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 
     * @param deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * 
     * @return
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Override
    @Column(name = "CREATED_AT")
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     */
    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     */
    @Basic
    @Column(name = "CREATED_BY")
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 
     * @param createdBy
     */
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 
     * @return
     */
    @Transient
    @Override
    public Date getModifiedAt() {
        return getCreatedAt();
    }

    /**
     * 
     * @param modifiedAt
     */
    @Override
    public void setModifiedAt(Date modifiedAt) {
        this.setCreatedAt(modifiedAt);
    }

    /**
     * 
     * @return
     */
    @Transient
    @Override
    public String getModifiedBy() {
        return getCreatedBy();
    }

    /**
     * 
     * @param modifiedBy
     */
    @Override
    public void setModifiedBy(String modifiedBy) {
        this.setCreatedBy(modifiedBy);
    }

    /**
     * 
     * @param kod
     */
    @Override
    public void setKod(String kod) {
        this.kod = kod;
    }

    @Basic
    @Column(name = "KOD")
    @Override
    public String getKod() {
        return kod;
    }    

    @Basic
    @Column(name = "VERZIO_NEV")
    @Override
    public String getVerzioNev() {
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

    @Basic
    @Column(name = "VERZIO_MEGJEGYZES")
    @Override
    public String getVerzioMegjegyzes() {
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

    /**
     * 
     * @return
     */
    @Transient
    @Override
    public String getPersistenceID() {
        return getId();
    }

    /**
     * 
     * @param persistenceID
     */
    @Override
    public void setPersistenceID(String persistenceID) {
        setId(persistenceID);
    }

    /**
     * @return Visszaadja a felületen megjeleníthető, felhasználó által értelmezhető verzió megnevezést.
     * A visszaadott verzió megnevezés tartalmazza a létrehozás dátumát, a létrehozó nevét,
     * valamint a verzió nevét.
     */
    @Transient
    public String getMegjelenithetoVerzio() {
        return VersionedObjectUtil.getMegjelenithetoVerzio(this);
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "UNDERLYING_XML", columnDefinition = "CLOB NOT NULL")
    @Override
    public String getXml() {
        return xml;
    }

    @Override
    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    @Basic(optional = false)
    @Column(name = "xmlversion", nullable = false)
    public Integer getXmlVersion() {
        return xmlVersion;
    }

    @Override
    public void setXmlVersion(Integer xmlVersion) {
        this.xmlVersion = xmlVersion;
    }
}
