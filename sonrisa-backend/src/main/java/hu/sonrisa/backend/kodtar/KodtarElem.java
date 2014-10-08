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

import hu.sonrisa.backend.model.Key;
import hu.sonrisa.backend.model.MapAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A kódtár ({@link Kodtar}) egy elemét (kódszavát) reprezentáló osztály.
 *
 * @author joe
 * @author cserepj
 */
@XmlRootElement(name = "ke")
public class KodtarElem implements Serializable, Comparable<KodtarElem>, Key {        

    private static final long serialVersionUID = 1L;
    
    /** A kódtár elem ID-je. */
    private String id;
        
    /** Az elem megnevezése. Ez kerül a felületre. */
    private String megnevezes;
    
    /** Annak a kodtarnak a kodja, amibe ez az elem tartozik. */
    private String kodtar;
        
    /** Szöveges megjegyzés a kódtár elemhez. */
    private String megjegyzes = "";
    
    /**
     * Az attribútumok
     */
    private Map<String, KodtarElemAttr> attributumok = new HashMap<String, KodtarElemAttr>();
    private Collection<KodtarElemAttr> attributumLista = new MapAdapter<KodtarElemAttr>(attributumok);
    
    // ------------------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------------------
    
    /**
     * Konstruktor a JAXB szamara.
     */
    private KodtarElem() {
    }
    
    /**
     * Az elem azonosítója, elnevezése és típusa alapján létrehoz egy elemet.
     * @param id A kódtár elem azonosítója
     * @param megnevezes A kódtár elem neve
     * @param kodtar A kódtár kódja, amibe az elem tartozik
     */
    public KodtarElem(String id, String megnevezes, String kodtar) {
        this.id = id;
        this.megnevezes = megnevezes;
        this.kodtar = kodtar;
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
    protected void afterUnmarshal(Unmarshaller u, Object parent) {}
    
    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------
    
    /**
     * Nev alapjan keres elem attributumra.
     * 
     * @param attrNev
     * @return 
     */
    public KodtarElemAttr findAttributum(String attrNev) {
        return attributumok.get(attrNev);
    }

    /**
     * Nev alapjan eltavolit egy attributumot, amennyiben az letezik.
     * 
     * @param nev
     * @return 
     */
    public KodtarElem removeAttributum(String attrNev) {
        attributumok.remove(attrNev);
        return this;
    }

    /**
     * Felvesz egy uj attributumot az elemre.
     * 
     * @param nev
     * @param ertek
     * @return 
     */
    public KodtarElem attributum(String attrNev, String ertek) {
        attributumok.put(attrNev, new KodtarElemAttr(this, attrNev, ertek));
        return this;
    }
    
    /**
     * Az alábbi attribútumok egyezőségét vizsgálja:
     * <ul>
     *  <li>{@link #id}</li>
     *  <li>{@link #kodtar}</li>
     *  <li>{@link #megnevezes}</li>
     *  <li>{@link #megjegyzes}</li>
     * </ul>
     * 
     * @param obj
     * @return 
     */    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KodtarElem other = (KodtarElem) obj;
        if ((this.megnevezes == null) ? (other.megnevezes != null) : !this.megnevezes.equals(other.megnevezes)) {
            return false;
        }
        if ((this.megjegyzes == null) ? (other.megjegyzes != null) : !this.megjegyzes.equals(other.megjegyzes)) {
            return false;
        }
        if ((this.kodtar == null) ? (other.kodtar != null) : !this.kodtar.equals(other.kodtar)) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
               
    @Override
    public int hashCode() {
        int hash = 3;        
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[név: " + megnevezes + ", id: " + id + "]";
    }

    /**
     * Id alapján dönti el az egyezőséget.
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(KodtarElem o) {
        return getId().compareTo(o.getId());
    }
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------

    @Override
    public String getKey() {
        return getId();
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "m")
    public String getMegnevezes() {
        return megnevezes;
    }

    public void setMegnevezes(String megnevezes) {
        this.megnevezes = megnevezes;
    }

    @XmlAttribute(name = "kt")
    public String getKodtar() {
        return kodtar;
    }

    public void setKodtar(String kodtar) {
        this.kodtar = kodtar;
    }

    @XmlAttribute(name = "megj")
    public String getMegjegyzes() {
        return megjegyzes;
    }

    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
    }

    @XmlElementRef
    public Collection<KodtarElemAttr> getAttributumLista() {
        return attributumLista;
    }      
}
