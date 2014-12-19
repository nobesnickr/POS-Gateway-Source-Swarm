/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sonrisa.backend.kodtar;

import hu.sonrisa.backend.model.Key;
import java.io.Serializable;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Kodtar egy elemen levo konkret attributum ertek.
 *
 * @author joe
 * @author cserepj
 */
@XmlRootElement(name="kea")
public class KodtarElemAttr  implements Key, Serializable{
    
    /** Ezen az elemen talalhato ez az attributum. */
    private KodtarElem elem;
    /** Az attributum erteke. */
    private String ertek;
    /** Az attributum neve. */
    private String nev;

    private KodtarElemAttr() {
    }

    public KodtarElemAttr(KodtarElem elem, String nev, String ertek) {
        this.ertek = ertek;
        this.nev = nev;
        this.elem = elem;
    }
    
    // ----------------------------------------------------------------------
    // ~ Lifecycle methods
    // ----------------------------------------------------------------------    
    
    /**
     * JAXB unmarshall után meghívódó metódus
     *
     * @param u
     * @param parent
     */
    protected void afterUnmarshal(Unmarshaller u, Object parent) {
        this.elem = (KodtarElem) parent;
    }            

    // ----------------------------------------------------------------------
    // ~ Public methods
    // ----------------------------------------------------------------------
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KodtarElemAttr other = (KodtarElemAttr) obj;
        if ((this.ertek == null) ? (other.ertek != null) : !this.ertek.equals(other.ertek)) {
            return false;
        }
        if ((this.nev == null) ? (other.nev != null) : !this.nev.equals(other.nev)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.nev != null ? this.nev.hashCode() : 0);
        return hash;
    }
    
    // ----------------------------------------------------------------------
    // ~ Getters / setters
    // ----------------------------------------------------------------------
    
    @Override
    public String getKey() {
        return nev;
    }
    
    
    @XmlAttribute(name = "e")
    public String getErtek() {
        return ertek;
    }

    public void setErtek(String ertek) {
        this.ertek = ertek;
    }

    @XmlAttribute(name = "n")
    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    @XmlTransient
    public KodtarElem getElem() {
        return elem;
    }

    public void setElem(KodtarElem elem) {
        this.elem = elem;
    }

    
}
