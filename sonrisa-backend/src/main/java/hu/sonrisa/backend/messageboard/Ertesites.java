/*
 *  *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.messageboard;

import hu.sonrisa.backend.entity.VersionedEntity;
import java.util.Date;
import javax.persistence.*;
import org.eclipse.persistence.annotations.Cache;

/**
 * Üzenőfalon használt értesítés entitás.
 *
 * @author kelemen
 */
@Entity
@Table(name = "ertesites")
@Cache(expiry = 300000, shared = true, refreshOnlyIfNewer = true)
public class Ertesites extends VersionedEntity<Long> {

    private Long id, version;
    private String felhasznalo;
    private String felhasznaloNev;
    private String pir;
    private String cim, bevezeto, szoveg;
    private Date date;

    /**
     * Constructor
     */
    public Ertesites() {
        super();
    }

    /**
     * Set id
     * @param id 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Id
    @SequenceGenerator(name = "S_ERTESITES", sequenceName = "S_ERTESITES", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_ERTESITES")
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Get version
     * @return 
     */
    @Version
    public Long getVersion() {
        return version;
    }

    /**
     * Set version
     * @param version 
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get bevezeto
     * @return
     */
    public String getBevezeto() {
        return bevezeto;
    }

    /**
     * Set bevezeto
     * @param bevezeto
     */
    public void setBevezeto(String bevezeto) {
        this.bevezeto = bevezeto;
    }

    /**
     * Get cim
     * @return
     */
    public String getCim() {
        return cim;
    }

    /**
     * Set cim
     * @param cim
     */
    public void setCim(String cim) {
        this.cim = cim;
    }

    /**
     * Get date
     * @return
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datum")
    public Date getDate() {
        return date;
    }

    /**
     * Set date
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get felhasznalo
     * @return
     */
    @Column(name = "felhasznalo_id")
    public String getFelhasznalo() {
        return felhasznalo;
    }

    /**
     * Set felhasznalo
     * @param felhasznalo
     */
    public void setFelhasznalo(String felhasznalo) {
        this.felhasznalo = felhasznalo;
    }

    /**
     * Get felhasznaloNev
     * @return
     */
    @Column(name = "felhasznalo_nev")
    public String getFelhasznaloNev() {
        return felhasznaloNev;
    }

    /**
     * Set felhasznaloNev
     * @param felhasznaloNev
     */
    public void setFelhasznaloNev(String felhasznaloNev) {
        this.felhasznaloNev = felhasznaloNev;
    }

    /**
     * Get pir
     * @return
     */
    public String getPir() {
        return pir;
    }

    /**
     * Set pir
     * @param pir
     */
    public void setPir(String pir) {
        this.pir = pir;
    }

    /**
     * Get szoveg
     * @return
     */
    @Lob
    public String getSzoveg() {
        return szoveg;
    }

    /**
     * Set szoveg
     * @param szoveg
     */
    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }
}
