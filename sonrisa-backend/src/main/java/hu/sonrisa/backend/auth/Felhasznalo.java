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
package hu.sonrisa.backend.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * A K11 rendszer egy felhasználóját reprezentáló osztály
 *
 * @author cserepj
 */
public final class Felhasznalo implements Serializable {

    private String id;

    private String szervezet;

    private String nev;

    private List<FelhasznaloSzerep> szerepek = new ArrayList<FelhasznaloSzerep>();

    /**
     * Konstruktor
     * @param id -
     * @param nev
     * @param szervezet
     * @param szerepek
     */
    public Felhasznalo(String id, String nev, String szervezet,  FelhasznaloSzerep ... szerepek) {
        setId(id);
        this.szervezet = szervezet;
        setNev(nev);
        this.szerepek.addAll(Arrays.asList(szerepek));
    }

    private Felhasznalo() {
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public List<FelhasznaloSzerep> getSzerepek() {
        return szerepek;
    }

    public List<String> getSzervezetek() {
        Set<String> result = new HashSet<String>();
        for (FelhasznaloSzerep szerep : szerepek){
            result.add(szerep.getSzervezet());
        }
        return new ArrayList<String>(result);
    }

    public void setSzerepek(List<FelhasznaloSzerep> szerepek) {
        this.szerepek = szerepek;
    }

    public String getSzervezet() {
        return szervezet;
    }

    public void setSzervezet(String szervezet) {
        this.szervezet = szervezet;
    }

    public List<String> getSzerepForPartner(String pirCode) {
        List<String> ret = new ArrayList<String>();
        for (FelhasznaloSzerep fsz : szerepek) {
            if (fsz.getSzervezet().equals(pirCode)) {
                ret.add(fsz.getSzerep());
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Felhasznalo other = (Felhasznalo) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.szervezet != null ? this.szervezet.hashCode() : 0);
        hash = 17 * hash + (this.nev != null ? this.nev.hashCode() : 0);
        hash = 17 * hash + (this.szerepek != null ? this.szerepek.hashCode() : 0);
        return hash;
    }
}
