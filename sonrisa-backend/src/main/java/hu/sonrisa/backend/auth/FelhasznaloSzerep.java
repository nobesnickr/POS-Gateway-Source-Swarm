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

/**
 * Egy szervezetfüggő felhasználói szerepkör objektumreprezentációja
 * @author cserepj
 */
public final class FelhasznaloSzerep {

    private String szerep;
    private String szervezet;

    private FelhasznaloSzerep() {
    }
    /**
     * Konstruktor
     * @param szerep
     * @param szervezet
     */
    public FelhasznaloSzerep(String szerep, String szervezet) {
        this.szerep = szerep;
        this.szervezet = szervezet;
    }

    public String getSzerep() {
        return szerep;
    }

    public String getSzervezet() {
        return szervezet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FelhasznaloSzerep other = (FelhasznaloSzerep) obj;
        if ((this.szerep == null) ? (other.szerep != null) : !this.szerep.equals(other.szerep)) {
            return false;
        }
        if ((this.szervezet == null) ? (other.szervezet != null) : !this.szervezet.equals(other.szervezet)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.szerep != null ? this.szerep.hashCode() : 0);
        hash = 23 * hash + (this.szervezet != null ? this.szervezet.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "[" + szervezet + ": " + szerep + "]";
    }
}