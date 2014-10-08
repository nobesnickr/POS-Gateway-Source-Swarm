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
package hu.sonrisa.backend.async;

import hu.sonrisa.backend.model.ResourceBasedUzenet;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Egy háttérfolyamat információit tartalmazó osztály
 * 
 * @author sonrisa
 */
public final class HatterFolyamatLeiro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String id;
    private List<ResourceBasedUzenet> uzenetek;
    private String tipus;
    private String futtato;
    private Date inditva;
    private boolean finished;
    private boolean megszakithato;
    private boolean megszakitott;

    /**
     * 
     */
    public HatterFolyamatLeiro() {
    }
    
    /**
     * 
     * @param hostId
     * @param hf 
     */
    HatterFolyamatLeiro(String hostId, HatterFolyamat hf) {
        this.hostId = hostId;
        this.id = hf.getId();
        this.uzenetek = hf.getUzenetek();
        this.finished = hf.isFinished();
        this.megszakithato = hf.isMegszakithato();
        this.tipus = hf.getClass().getSimpleName();
        this.inditva = hf.getIndulas();
        this.futtato = hf.getFuttato();
        this.megszakitott = hf.isMegszakitott();
    }

    /**
     * 
     * @return
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @return
     */
    public List<ResourceBasedUzenet> getUzenetek() {
        return uzenetek;
    }

    /**
     * 
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * 
     * @return
     */
    public boolean isMegszakithato() {
        return megszakithato;
    }

    /**
     * 
     * @return
     */
    public String getTipus() {
        return tipus;
    }

    /**
     * 
     * @return
     */
    public String getFuttato() {
        return futtato;
    }

    /**
     * 
     * @return
     */
    public Date getInditva() {
        return inditva;
    }
    
    public boolean isMegszakitott() {
        return megszakitott;
    }
    
    /**
     * 
     * @param hostId
     * @return
     */
    public HatterFolyamatLeiro clone(String hostId) {
        HatterFolyamatLeiro hfl = new HatterFolyamatLeiro();
        hfl.hostId = hostId;
        this.id = this.getId();
        this.uzenetek = this.getUzenetek();
        this.finished = this.isFinished();
        this.megszakithato = this.isMegszakithato();
        this.tipus = this.getClass().getSimpleName();
        this.inditva = this.getInditva();
        this.futtato = this.getFuttato();

        return hfl;
    }
    
}
