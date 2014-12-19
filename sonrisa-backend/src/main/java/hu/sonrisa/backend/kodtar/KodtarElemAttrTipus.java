/*
 *   Copyright (c) 2012 Sonrisa Informatikai Kft. All Rights Reserved.
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

package hu.sonrisa.backend.kodtar;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A kodtar elemein felveheto attributumok definialasara szolgalo osztaly.
 *
 * @author joe
 * @author cserepj
 */
@XmlRootElement(name = "keat")
public class KodtarElemAttrTipus implements Serializable{

     private static final String DEFAULT_TIPUS_NAME = "-";
     
    /**
     * Egy kódtár nevét (id) adja meg.
     * 
     * Ha meg van adva, akkor az attributum ennek a kodtarnak az elemei kozul
     * vehet fel ertekeket.
     * 
     * Ha null, akkor az attribútum nem egy másik kódtárból veszi az elemeit,
     * hanem sima szöveges attribútum lesz, lsd {@link #isSzabadSzoveges() }
     */
    private String tipus;
    
    /**
     * Az attribútum nevét adja meg
     */
    private String nev;
    
    // ------------------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------------------
    
    /**
     * Konstruktor a JAXB szamara.
     */
    private KodtarElemAttrTipus() {}

    /**
     * 
     * @param nev
     * @param tipus 
     */
    public KodtarElemAttrTipus(String nev, String tipus) {
        this.tipus = tipus;
        this.nev = nev;
    }
    
    /**
     * Masolo konstruktor.
     * 
     * @param attr 
     */
    public KodtarElemAttrTipus(KodtarElemAttrTipus attr) {
        this(attr.getNev(), attr.getTipus());
    }
    
    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------  
    
    /**
     * Visszaadja, hogy az attributum szabad szoveges-e,
     * vagy egy masik kodtar elemei kozul veszi az ertekeit.
     * 
     * @return true, ha szabadszoveges
     */
    @XmlTransient
    public boolean isSzabadSzoveges(){
        return tipus == null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KodtarElemAttrTipus other = (KodtarElemAttrTipus) obj;
        if ((this.nev == null) ? (other.nev != null) : !this.nev.equals(other.nev)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.nev != null ? this.nev.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "KodtarElemAttrTipus{" + "tipus=" + tipus + ", nev=" + nev + '}';
    }
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------    

    @XmlAttribute(name = "n")
    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    @XmlAttribute(name = "t")
    public String getTipus() {
        return tipus;
    }

    /**
     * Felületbarát típus nevet ad vissza.
     * Ha a típus null, akkor egy default értékkel helyettesíti.
     *
     * @return
     */
    @XmlTransient
    public String getTipusName() {
        return tipus != null ? tipus : DEFAULT_TIPUS_NAME;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    } 
}
