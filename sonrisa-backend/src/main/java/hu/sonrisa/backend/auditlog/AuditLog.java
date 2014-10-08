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
package hu.sonrisa.backend.auditlog;

import hu.sonrisa.backend.entity.VersionedEntity;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Audit jellegű naplózást megvalósító entity
 * @author sonrisa
 */
@Entity()
@Table(name = "audit_log")
@Access(AccessType.PROPERTY)
public class AuditLog extends VersionedEntity<Long> {

    private static final long serialVersionUID = 1L;
    private Long id, version;
    private Date idopont;
    private String felhasznaloId;
    private String felhasznaloNev;
    private String esemeny;
    private String parameter;
    private String host;
    private String szervezet;

    /**
     * Konstruktor
     */
    public AuditLog() {
    }

    /** 
     * JPA ID
     * 
     * @return
     */
    @Id
    @SequenceGenerator(name = "S_AUDIT_LOG", sequenceName = "S_AUDIT_LOG", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_AUDIT_LOG")    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     *
     * @return
     */    
    public String getEsemeny() {
        return esemeny;
    }

    /**
     *
     * @param esemeny
     */
    public void setEsemeny(String esemeny) {
        this.esemeny = esemeny;
    }

    /**
     * 
     * @param e
     */
    public void setEsemeny(Enum<?> e) {
        this.esemeny = e.name();
    }

    /**
     *
     * @return
     */
    @Column(name = "FELHASZNALO_ID")
    public String getFelhasznaloId() {
        return felhasznaloId;
    }

    /**
     *
     * @param felhasznaloId
     */
    public void setFelhasznaloId(String felhasznaloId) {
        this.felhasznaloId = felhasznaloId;
    }

    /**
     *
     * @return
     */
    @Column(name = "FELHASZNALO_NEV")
    public String getFelhasznaloNev() {
        return felhasznaloNev;
    }

    /**
     *
     * @param felhasznaloNev
     */
    public void setFelhasznaloNev(String felhasznaloNev) {
        this.felhasznaloNev = felhasznaloNev;
    }

    /**
     *
     * @return
     */
    @Temporal(TemporalType.TIMESTAMP)    
    public Date getIdopont() {
        return idopont;
    }

    /**
     *
     * @param idopont
     */
    public void setIdopont(Date idopont) {
        this.idopont = idopont;
    }

    /**
     *
     * @return
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * 
     * @param parameter
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     *
     * @return
     */    
    public String getSzervezet() {
        return szervezet;
    }

    /**
     *
     * @param szervezet
     */
    public void setSzervezet(String szervezet) {
        this.szervezet = szervezet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditLog other = (AuditLog) obj;
        if (this.id != null ? !this.id.equals(other.getId()) : other.getId() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AuditLog{" + "id=" + id + ", version=" + version + ", idopont=" + idopont + 
                ", felhasznaloId=" + felhasznaloId + ", felhasznaloNev=" + felhasznaloNev + ", esemeny=" + esemeny + 
                ", parameter=" + parameter + ", host=" + host + ", szervezet=" + szervezet + '}';
    }
}
