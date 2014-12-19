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
import hu.sonrisa.backend.versionedobject.VersionedObjectBase;
import hu.sonrisa.backend.model.util.DateUtil;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Egy kódtárat (kódcsoportot) jelképező osztály.
 * 
 * Figyelni kell, ha setter-ekkel bovitjuk! 
 * A setter-ek dirty-re allitjak a kodtarat, 
 * ha a setter valoban megvaltoztatja a beallitott erteket.
 * Egyeb muveletek eseten (elem vagy attr hozzaadas/torles)
 * szinten dirty-re lesz allitva a kodtar, ha valoban tortenik erdemi valtozas.
 *
 * @author joe
 * @author cserepj
 */
@XmlRootElement(name = "kt")
@XmlSeeAlso({KodtarElem.class, KodtarElemAttrTipus.class})
public class Kodtar extends VersionedObjectBase implements Comparable<Kodtar>, Key{
                  
    private static final long serialVersionUID = 1L;
    
    /** A kódtár, és azon belül a kódtár elemek jelenlegi XML_VERZIO-ja. */
    private static final int XML_VERZIO = 1;
    
    /**
     * A megengedhető maximális hossza a kodtarhoz adható megjegyzésnek.
     */
    public static final int MAX_MEGJEGYZES_LENTGH = 3000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Kodtar.class);
   
    /** Ennyivel kell eltolni egy elemet, ha egyel akarjuk felfele mozgatni. */
    public static int ELEM_FEL = -1;
    /** Ennyivel kell eltolni egy elemet, ha egyel akarjuk lefele mozgatni. */
    public static int ELEM_LE = 1;
    
    // -----------------------------------------------------------------------
    // ~ Private members
    // -----------------------------------------------------------------------

    /**  A kódtár megnevezése. */    
    private String megnevezes;
    
    /** Szöveges megjegyzés a kódtárhoz. */    
    private String megjegyzes = "";
    
    /** Változott-e valami a kódtáron, kell-e menteni. */
    private boolean dirty = false;    
    
    /** 
     * Ha a kódtár érvényessége dátumhoz kötött, akkor ez a dátum határozza meg az érvényesség kezdetét. 
     * Végtelen = null.
     */    
    private Date ervenyessegKezdete;
    
    /** 
     * Ha a kódtár érvényessége dátumhoz kötött, akkor ez a dátum határozza meg az érvényesség végét. 
     * Végtelen = null.
     */
    private Date ervenyessegVege;
    
    /** 
     * A kódtár elemein értelmezett attribútumok listája. 
     * Ez tulajdonképpen a definíciója a kódtár egyes elemein megadható attribútumoknak.
     * 
     * Lsd: {@link KodtarElemAttrTipus}
     */
    private Set<KodtarElemAttrTipus> attributumok = new HashSet<KodtarElemAttrTipus>();
   
    /**
     * A kódtár elemei (kódszavai).
     * Együtt perzisztálódnak a kódtárral, külön életet nem élnek.
     */
    private MapAdapter<KodtarElem> kodtarElemek =
            new MapAdapter(new LinkedHashMap<String, KodtarElem>());
    
    // ------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------
    /**
     * 
     */
    public Kodtar() {
        this(null, null);
    }

    /**
     * 
     * @param kod
     * @param megnevezes
     */
    public Kodtar(String kod, String megnevezes) {
        super(XML_VERZIO);
        setKod(kod);
        this.megnevezes = megnevezes;
        setPersistenceID(UUID.randomUUID().toString());                
    }
    
    /**
     * 
     * @param kod
     * @param megnevezes
     * @param ervenyessegKezdete 
     * @param ervenyessegVege 
     */
    public Kodtar(String kod, String megnevezes, Date ervenyessegKezdete, Date ervenyessegVege) {
        this(kod, megnevezes);
        this.ervenyessegKezdete = ervenyessegKezdete;
        this.ervenyessegVege = ervenyessegVege;
    }
           
    // ------------------------------------------------------------------------
    // ~ Public methods
    // ------------------------------------------------------------------------                
    
    /**
     * Uj attributum hozzaadasa.
     * 
     * @param attrTip
     * @return 
     */
    public Kodtar addAttributum(KodtarElemAttrTipus attrTip){
        if (attrTip != null){
            final boolean added = attributumok.add(attrTip);
            if (added){
                // ha valoban hozza lett adva attr, akkor dirty-nek kell jelolni
                dirty = true;
            }
        }
        return this;
    }
    
    /**
     * Attributum eltavolitasa.
     * 
     * @param attrTip
     * @return 
     */
    public boolean removeAttributum(KodtarElemAttrTipus attrTip){
        final boolean removed = attributumok.remove(attrTip);
        if (removed){
            // ha valoban torlodott attributum, akkor dirty-nek kell jelolni
            dirty = true;
        }
        return removed;
    }
    
    /**
     * Uj elem (elemek) hozzaadasa a kodtarhoz.
     * Csak olyan elemet lehet hozzaadni, ami ehhez a kodtarhoz tartozik.
     * 
     * @param elem
     * @return
     */
    public Kodtar addElem(KodtarElem... elem) {
        for (KodtarElem e : elem) {
            //ha van padding az elemID-kre, akkor itt ez kelleni fog, jelenleg nincs
//            if (isFixHosszuElemId()) {
//                String padded = padDimElemId(e.getId());
//                if (padded.length() != getFixIdHossz()) {
//                    LOGGER.error("Hibás id hossz: " + e);
//                    continue;
//                } else {
//                    e.setId(padded);
//                }
//            }
            if (!e.getKodtar().equals(getKod())) {
                LOGGER.error("Kodtar elem tipusa nem azonos a kodtar koddal: '" + e.getKodtar() + "' != '" + getKod() + "'");
                throw new IllegalArgumentException(e.getId() + " kodtar elem itpusa nem azonos a kodtar koddal:" + e.getKodtar()+ " != " + getKod());
            }
            kodtarElemek.add(e);
            dirty = true;   // uj elemet adtunk a kodtarhoz, dirty-nek kell jelolni
        }
        return this;
    }
    
    /**
     * A kapott elem mozgatasa a megadott lepessel az elemek kozott.
     * </p>
     * 
     * Ha az elem nem talalhato meg a kodtaron levo elemek kozott, 
     * akkor nem csinal semmit.
     * <p/>    
     * Ha a lepes pozitiv, akkor a vege fele lepteti az elemet,
     * ha a lepes negativ, akkor a lista eleje fele. Tobb mint egy 
     * lepest is meg tud tenni egyszerre.
     * <p/>
     * Ha a mozgatas soran az elem eler a lista vegere/elejere,
     * es meg tovabb kell lepnie, akkor "atfordul" es onnan
     * folytatja a lepkedest tovabb.
     * 
     * 
     * @param elem
     * @param lepes az elem lepeseinek szama (es iranya)
     */
    public void elemMozgatas(KodtarElem elem, int lepes) {

        // csak nem null es letezo elemmel mukodik
        if (elem != null && getKodtarElemek().contains(elem)) {
            // atmasolja az elemeket egy listaba
            final List<KodtarElem> elemList = new ArrayList<KodtarElem>(getElemek());

            // a lista merete, azaz az elemek szama
            final int size = getElemek().size();
            // az athelyezni kivant elem aktualis indexe
            final int actIntex = elemList.indexOf(elem);

            // tuti meg kell legyen, de azert nem art meg egy 
            // plusz ellenorzes
            if (actIntex > -1) {
                // kiveszi az elemet a jelenlegi helyerol
                elemList.remove(elem);

                // modulo osztas, mivel  "felesleges" a meretnel nagyobb 
                // erteket megadni, ugyis atfordul
                lepes = lepes % size;

                // kiszamitja hova kene eltolni 
                int ujIndex = actIntex + lepes;

                // ha negativ ertek jon ki, akkor a merethez hozza kell adni, 
                // mintha a vegerol elolrol kezdenenk a lepkedest visszafele
                if (ujIndex < 0){
                    ujIndex = size + ujIndex;
                }

                // ha tulcsordul, atfordul
                ujIndex = ujIndex % size;
                
                // ha esetleg pont az utolso helyre kell hozzaadni                
                if (ujIndex >= size) {
                    // a vegere rakja
                    elemList.add(elem);
                } else {
                    // kulonben pedig a liszamolt uj helyre berakja
                    elemList.add(ujIndex, elem);
                }
                
                // most mar csak vissza kell frissiteni az elemeket
                elemekUjraHozzaadasa(elemList);
            }

        }
    }
    
    
    /**
     * Kodtar elemeket kod szerint rendezo metodus.
     * 
     * @return 
     */
    public Kodtar sortElemekByKod() {
        // atmasolja az elemeket egy listaba
        final List<KodtarElem> elemList = new ArrayList<KodtarElem>(getElemek());

        Collections.sort(elemList, new Comparator<KodtarElem>() {
            public int compare(KodtarElem e1, KodtarElem e2) {
                return e1.getKey().compareTo(e2.getKey());
            }
        });
        // most mar csak vissza kell frissiteni az elemeket
        elemekUjraHozzaadasa(elemList);

        return this;
    }
    
    /**
     * Uj elemek hozzaadasa a kodtarhoz.
     * Csak olyan elemet lehet hozzaadni, ami ehhez a kodtarhoz tartozik.
     * 
     * @param elemek
     * @return 
     */
    public Kodtar addAllElem(Collection<KodtarElem> elemek){
        return addElem(elemek.toArray(new KodtarElem[elemek.size()]));
    }
    
    /**
     * Elem eltavolitasa.
     * 
     * @param elem
     * @return 
     */
    public boolean removeElem(KodtarElem elem){
        final boolean removed = kodtarElemek.remove(elem);
        if (removed){
            // ha valoban torlodott elem, akkor dirty-nek kell jelolni
            dirty = true;
        }
        return removed;
    }

    /**
     * Meghívja a kódtáron található elemekre a {@link #getKodtarElemek() } metódust
     * Törli az összes elemet, majd egyesével ({@link #addElem() ) újra hozzáadja őket,
     * azonban ellenőrzi őket, hogy megfelelnek a formai szabályoknak.
     */
    public void refreshElemek() {
        Collection<KodtarElem> regiElemek = new ArrayList<KodtarElem>(getElemek());
        kodtarElemek.clear();
        for (KodtarElem de : regiElemek) {
            addElem(de);
        }
    } 
    
    /**
     * Kodtar elem attribútumot ad vissza név alapján.
     * <p/>
     *
     * Ha nincs ilyen attribútum típus, akkor null-t ad vissza.
     *
     * @param name keresett attribútum neve
     * @return addott nevű attribútum, ha létezik, <br/>különben null
     */
    public KodtarElemAttrTipus findElemAttrTipusByName(String name) {
        for (KodtarElemAttrTipus attrTipus : getAttributumok()) {
            if (attrTipus.getNev().equals(name)) {
                return attrTipus;
            }
        }
        return null;
    }
    
    /**
     * A elmek közül visszaadja a kapott ID-val rendelkezőt.
     *
     * Ha a kapott elemId null, akkor null-t ad vissza.
     *
     * @param elemId
     * @return
     */
    public KodtarElem findElemById(String elemId) {
        KodtarElem result = null;
        if (elemId != null) {
            //ha van padding az elemID-kre, akkor itt ez kelleni fog, jelenleg nincs
            //String paddedElemId = padDimElemId(elemId);
            result = kodtarElemek.get(elemId);
        }
        return result;
    }    
    
    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Kodtar o) {
        int ret = getKod().toLowerCase().compareTo(o.getKod().toLowerCase());
        if (ret != 0) {
            return ret;
        } else {
            return getCreatedAt().compareTo(o.getCreatedAt());
        }
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Kodtar{");
        strBuilder.append("kod=");
        strBuilder.append(getKod());
        strBuilder.append(", megnevezes=");                
        strBuilder.append(getMegnevezes());
        strBuilder.append(", dirty=");
        strBuilder.append(isDirty());
        strBuilder.append(", megjegyzes=");                
        strBuilder.append(getMegjegyzes());
        strBuilder.append(", deleted=");                
        strBuilder.append(isDeleted());
        strBuilder.append("}");
        return strBuilder.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.kodtarElemek != null ? this.kodtarElemek.hashCode() : 0);
        hash = 59 * hash + (this.megnevezes != null ? this.megnevezes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Kodtar other = (Kodtar) obj;
        if (this.kodtarElemek != other.kodtarElemek && (this.kodtarElemek == null || !this.kodtarElemek.equals(other.kodtarElemek))) {
            return false;
        }
        if ((this.megnevezes == null) ? (other.megnevezes != null) : !this.megnevezes.equals(other.megnevezes)) {
            return false;
        }
        return true;
    }
    
    /**
     * Visszaadja, hogy az adott kódtár érvényes-e a megadott dátumkor.
     * 
     * @param date
     * @return 
     */
    public boolean isErvenyes(final Date date){
        if (date == null) {
            // mert arra helytelenul mindig true-t ad vissza
            throw new IllegalArgumentException("Kodtar ervenyesseg vizsgalatnal a bejovo argumentum nem lehet null!");
        }
        return DateUtil.overlap(getErvenyessegKezdete(), getErvenyessegVege(), date, date);
    }
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------    
    
    /**
     * Tipikusan setterek altal hasznalt seged metodus.
     * Megvizsgalja a ket kapott erteket, es ha a ketto kulonbozik,
     * akkor dirty-nek allitja be a kodtarat.
     * 
     * @param oldValue
     * @param newValue 
     */
    private void markDirtyIfChanged(Object oldValue, Object newValue){
        boolean equals = 
               oldValue == null && newValue == null 
            || oldValue != null && oldValue.equals(newValue);
        
        // ha dirty volt az is marad
        // kulonben ha nem egyezik, akkor dirty lesz
        dirty = dirty || !equals;
    }
    
    // ------------------------------------------------------------------------
    // ~ Getters / setters
    // ------------------------------------------------------------------------
    
    @Override
    public String getKey() {
        return getKod();
    }


    
    /**
     * Visszaadja, hogy tortent-e valtoztas a kodtaron
     * a legutolso mentes ota.
     * 
     * @return 
     */
    @XmlTransient
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @XmlAttribute(name = "megj")
    public String getMegjegyzes() {
        return megjegyzes;
    }

    /**
     * Uj megjegyzest allit be a kodtaron.
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param megjegyzes 
     */
    public void setMegjegyzes(String megjegyzes) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(this.megjegyzes, megjegyzes);        
        this.megjegyzes = megjegyzes;
    }

    @XmlAttribute(name = "megn")    
    public String getMegnevezes() {
        return megnevezes;
    }

    /**
     * Uj megnevezest allit ba a kodtarnak.
     * 
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param megnevezes 
     */
    public void setMegnevezes(String megnevezes) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(this.megnevezes, megnevezes);        
        this.megnevezes = megnevezes;
    }

    /**
     * JAXB részére
     * 
     * @return 
     */
    @XmlElementWrapper(name = "elemek")
    @XmlElementRef
    private Collection<KodtarElem> getElemek() {
        return kodtarElemek;
    }
    
    /**
     * Kodtar elemek lekerese.
     * 
     * <p/>
     * <strong>Nem modosithato kollekciot ad vissza!</strong><br/>
     * Uj elem hozzaadasara az 
     * {@link #addElem(hu.sonrisa.backend.kodtar.KodtarElem[]) } metodus hasznalhato.
     * <br/>
     * Elem torlesere a 
     * {@link #removeElem(hu.sonrisa.backend.kodtar.KodtarElem) } metodus hasznalhato.
     * 
     * @return 
     */
    @XmlTransient
    public Collection<KodtarElem> getKodtarElemek() {        
        return Collections.unmodifiableCollection(kodtarElemek);
    }    
                
    /**
     * Attributumok lekerese.
     * <p/>
     * 
     * <strong>Nem modosithato kollekciot ad vissza!</strong>
     * <br/>
     * 
     * Uj attributum hozzaadasara az 
     * {@link #addAttributum(hu.sonrisa.backend.kodtar.KodtarElemAttrTipus) } metodus hasznalhato.
     * <br/>
     * Attributum torlesere a
     * {@link #removeAttributum(hu.sonrisa.backend.kodtar.KodtarElemAttrTipus) } metodus hasznalhato.
     * @return
     */
    @XmlTransient
    public Set<KodtarElemAttrTipus> getAttributumok() {
        return Collections.unmodifiableSet(attributumok);
    }
    
    /**
     * JAXB szamara
     * 
     * @return
     */
    @XmlElementRef
    private Set<KodtarElemAttrTipus> getAttrs() {
        return attributumok;
    }

    @XmlAttribute(name = "ervK")
    public Date getErvenyessegKezdete() {
        return ervenyessegKezdete;
    }

    /**
     * Uj ervenyesseg kezdetet allit be a kodtaron.
     * 
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param ervenyessegKezdete 
     */
    public void setErvenyessegKezdete(Date ervenyessegKezdete) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(this.ervenyessegKezdete, ervenyessegKezdete);
        this.ervenyessegKezdete = ervenyessegKezdete;
    }

    @XmlAttribute(name = "ervV")
    public Date getErvenyessegVege() {
        return ervenyessegVege;
    }

    /**
     * Uj ervenyesseg veget allit ba a kodtaron.
     * 
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param ervenyessegVege 
     */
    public void setErvenyessegVege(Date ervenyessegVege) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(this.ervenyessegVege, ervenyessegVege);
        this.ervenyessegVege = ervenyessegVege;
    }   

    /**
     * Uj verzio nevet allit be a kodtaron.
     * 
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param verzioNev 
     */
    @Override
    public void setVerzioNev(String verzioNev) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(getVerzioNev(), verzioNev);
        super.setVerzioNev(verzioNev); 
    }

    /**
     * Uj verzio megjegyzest allit be a kodtaron.
     * 
     * Ha az uj ertek elter a regitol, akkor
     * dirty-re allitja a kodtarat.
     * 
     * @param verzioMegjegyzes 
     */
    @Override
    public void setVerzioMegjegyzes(String verzioMegjegyzes) {
        // ha a beallitott uj ertek nem egyezik a regivel, akkor dirty lesz
        markDirtyIfChanged(getVerzioMegjegyzes(), verzioMegjegyzes);
        super.setVerzioMegjegyzes(verzioMegjegyzes);
    }

    /**
     * Kitorli a jelenleg letezo kodtar elemeket,
     * es a kapott listaban levoket adja hozza helyettuk.
     * 
     * @param elemList 
     */
    private void elemekUjraHozzaadasa(final List<KodtarElem> elemList) {    
        kodtarElemek.clear();
        for (KodtarElem ke : elemList) {
            addElem(ke);
        }
        dirty = true;
    }
}
