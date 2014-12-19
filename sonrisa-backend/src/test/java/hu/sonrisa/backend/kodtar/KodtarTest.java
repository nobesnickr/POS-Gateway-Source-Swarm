/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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

import hu.sonrisa.backend.BackendDbTestBase;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A {@link Kodtar} osztalyt tesztelo tesztesetek kerulnek ide.
 *
 * @author joe
 */
public class KodtarTest extends BackendDbTestBase{
    
    @Autowired
    private KodtarService kodtarService;
    
               
    /**
     * Test of isErvenyes method, of class Kodtar.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testIsErvenyesNull() {        
        Date kezd = getDate(2000, Calendar.JANUARY, 1);
        Date veg = getDate(2000, Calendar.DECEMBER, 31);
        Kodtar kodtar = new Kodtar("k1", "k1m", kezd, veg);
        
        kodtar.isErvenyes(null);
    }
    
    /**
     * Test of isErvenyes method, of class Kodtar.
     */
    @Test
    public void testIsErvenyes() {        
        Date elotte = getDate(1999, Calendar.JANUARY, 1);
        Date kozben = getDate(2000, Calendar.JULY, 1);
        Date utana = getDate(2001, Calendar.JANUARY, 1);
        
        // mindkét végén nyitott kódtár
        Kodtar kodtar = new Kodtar("k1", "k1m");                
        assertTrue(kodtar.isErvenyes(kozben));        
        
        // véges érvényesség kezdete, végtelen érvényesség vége
        Date kezd = getDate(2000, Calendar.JANUARY, 1);
        kodtar.setErvenyessegKezdete(kezd);
        assertTrue(kodtar.isErvenyes(kozben));        
        assertFalse(kodtar.isErvenyes(elotte));        
        assertTrue(kodtar.isErvenyes(utana));
                                
        // végtelen érvényesség kezdete, véges érvényesség vége
        Date veg = getDate(2000, Calendar.DECEMBER, 31);
        kodtar.setErvenyessegKezdete(null);
        kodtar.setErvenyessegVege(veg);
        assertTrue(kodtar.isErvenyes(elotte));        
        assertTrue(kodtar.isErvenyes(kozben));        
        assertFalse(kodtar.isErvenyes(utana));        
                
        // véges érvényesség kezdete, véges érvényesség vége
        kodtar.setErvenyessegKezdete(kezd);
        kodtar.setErvenyessegVege(veg);
        // elotte, kozben, utana
        assertTrue(kodtar.isErvenyes(kozben));
        assertFalse(kodtar.isErvenyes(elotte));
        assertFalse(kodtar.isErvenyes(utana));
        // vegpontokban
        assertTrue(kodtar.isErvenyes(kezd));
        assertTrue(kodtar.isErvenyes(veg));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRefreshElemek(){
        Kodtar kodtar = new Kodtar("k1", "k1m");
        KodtarElem e1 = new KodtarElem("elem1", "elem1megnev", kodtar.getKod());
        KodtarElem e2 = new KodtarElem("elem2", "elem2megnev", kodtar.getKod());
        kodtar.addElem(e1);
        kodtar.addElem(e2);
        
        // ronstuk el az elemen a kodtar kodot
        e2.setKodtar("rossz-kodtar-kod");
        
        kodtar.refreshElemek();                               
    }
    
    /**
     * Teszt: Attributummal kapcsolatos tesztek
     */        
    @Test
    public void testAttr(){
        // teszt adat
        final String attr1Name = "attr1";
        final String attr2Name = "attr2";
        Kodtar kodtar = new Kodtar("k1", "k1m");
        kodtar.addAttributum(new KodtarElemAttrTipus(attr1Name, null));
        kodtar.addAttributum(new KodtarElemAttrTipus(attr2Name, null));
        
        // attr kereses nev alapjan a kodtaron
        final KodtarElemAttrTipus attr1 = kodtar.findElemAttrTipusByName(attr1Name);
        assertNotNull(attr1);
        assertEquals(attr1Name, attr1.getNev());                                        
        
        // elem hozzaadasa a kodtarhoz
        KodtarElem e1 = new KodtarElem("e1", "m1", kodtar.getKod());
        e1.attributum(attr1Name, "attr1Value");
        e1.attributum(attr2Name, "attr2Value");
        kodtar.addElem(e1);
        
        // attr kerese az elemen
        KodtarElemAttr attr2FromElem = e1.findAttributum(attr2Name);
        assertNotNull(attr2FromElem);
        assertEquals(attr2Name, attr2FromElem.getNev());
        
        // osszes attr elemrol
        assertEquals(2, e1.getAttributumLista().size());
        
        // attr torlese elemrol
        e1.removeAttributum(attr2Name);
        attr2FromElem = e1.findAttributum(attr2Name);
        assertNull(attr2FromElem);
        
        // attr torlese kodtarrol
        kodtar.removeAttributum(attr1);
        // elemen tovabbra is megmarad - kerdes: jo ez igy?
        assertNotNull(e1.findAttributum(attr1Name));
    }
    
    /**
     * Teszt: a kodtar kulonbozo setter metodusai dirty-re allitjak kodtarat.
     */
    @Test
    public void testIsDirty_setters(){
        Kodtar kodtar = new Kodtar("k1", "megn");
        kodtar.setMegjegyzes("megj");
        kodtar.setVerzioMegjegyzes("verz megj");
        kodtar.setVerzioNev("v1");
        kodtar.setErvenyessegKezdete(new Date());
        kodtar.setErvenyessegVege(new Date());        
        //letrehozas utan dirty, amig mentve nem lesz
        kodtar = assertDirtyAndSave(kodtar);
                
        // megnevezes
        kodtar.setMegnevezes(kodtar.getMegnevezes());
        assertFalse(kodtar.isDirty());
        kodtar.setMegnevezes("uj nev");
        kodtar = assertDirtyAndSave(kodtar);
        
        // megjegyzes
        kodtar.setMegjegyzes(kodtar.getMegjegyzes());
        assertFalse(kodtar.isDirty());
        kodtar.setMegjegyzes("valami megjegyzes");
        kodtar = assertDirtyAndSave(kodtar);
        
        // verzionev
        kodtar.setVerzioNev(kodtar.getVerzioNev());
        assertFalse(kodtar.isDirty());
        kodtar.setVerzioNev("v2");
        kodtar = assertDirtyAndSave(kodtar);
        
        // verzio megjegyzes
        kodtar.setVerzioMegjegyzes(kodtar.getVerzioMegjegyzes());
        assertFalse(kodtar.isDirty());
        kodtar.setVerzioMegjegyzes("uj verz megjegyzes");
        kodtar = assertDirtyAndSave(kodtar);
        
        // ervenyesseg kezd
        kodtar.setErvenyessegKezdete(kodtar.getErvenyessegKezdete());
        assertFalse(kodtar.isDirty());
        kodtar.setErvenyessegKezdete(new Date());
        kodtar = assertDirtyAndSave(kodtar);
        
        // ervenyesseg vege
        kodtar.setErvenyessegVege(kodtar.getErvenyessegVege());
        assertFalse(kodtar.isDirty());
        kodtar.setErvenyessegVege(new Date());
        kodtar = assertDirtyAndSave(kodtar);
    }
        
    /**
     * Teszt: a kodtar elem es attributum modosito metodusai dirty-re allitjak kodtarat.
     */
    @Test
    public void testIsDirty_addRemove(){
        Kodtar kodtar = new Kodtar("k1", "megn");
        // elem hozzaadas        
        final String kod = kodtar.getKod();
        final KodtarElem elem1 = new KodtarElem("e1", "m1", kod);
        kodtar.addElem(elem1);
        kodtar = assertDirtyAndSave(kodtar);
        kodtar.addAllElem(Arrays.asList(new KodtarElem("e2", "m2", kod)));
        kodtar = assertDirtyAndSave(kodtar);
        
        // elem torles
        kodtar.removeElem(elem1);
        kodtar = assertDirtyAndSave(kodtar);
        //ha nincs valodi torles (mert mar egyszer torolve lett az elem), akkor nem lesz dirty
        kodtar.removeElem(elem1);
        assertFalse(kodtar.isDirty()); 
        //null-nal se lesz dirty
        kodtar.removeElem(null);
        assertFalse(kodtar.isDirty()); 
        
        // attr hozzaadas
        final KodtarElemAttrTipus attr = new KodtarElemAttrTipus("attr1", null);
        kodtar.addAttributum(attr);
        kodtar = assertDirtyAndSave(kodtar);
        // mégegyszer ugyanazt hozzaadva, mar nem lesz dirty
        kodtar.addAttributum(attr);
        assertFalse(kodtar.isDirty());         
        
        // attr torles
        kodtar.removeAttributum(attr);
        kodtar = assertDirtyAndSave(kodtar);
        // mégegyszer ugyanazt torolve, mar nem lesz dirty
        kodtar.removeAttributum(attr);
        assertFalse(kodtar.isDirty());
    }    
    
    /**
     * Teszt: elem torlese
     */
    @Test
    public void testRemoveElem(){      
        Kodtar kodtar = new Kodtar("k1", "k1m");
        KodtarElem elem = new KodtarElem("elem1", "elem1megnev", kodtar.getKod());
        kodtar.addElem(elem);
        KodtarElem elem2 = new KodtarElem("elem2", "elem2megnev", kodtar.getKod());
        kodtar.addElem(elem2);
        
        assertEquals(2, kodtar.getKodtarElemek().size());
        
        // egyik torlese
        kodtar.removeElem(elem);
        assertEquals(1, kodtar.getKodtarElemek().size());
        assertNull(kodtar.findElemById(elem.getId()));        
        assertNotNull(kodtar.findElemById(elem2.getId()));  // masik meg megvan        
        
        // null inputtal nem tortenik semmi
        kodtar.removeElem(null);
        // meg mindig megvan        
        assertEquals(1, kodtar.getKodtarElemek().size());
        assertNotNull(kodtar.findElemById(elem2.getId()));  
    }
    
    /**
     * Teszteset az elemek mozgatasara.
     * Par specialis lehetoseget tesztel
     * 
     */
    @Test
    public void mozgatasTestSpecEsetek(){
        final Kodtar kodtar = new Kodtar("k1", "k1m");
        final KodtarElem e0 = new KodtarElem("e0", "megnev0", kodtar.getKod());
        final KodtarElem masElem = new KodtarElem("mas", "mas", "mas");
        
        // nincs meg elem hozzaadva (nem lehet kivetel)
        kodtar.elemMozgatas(e0, Kodtar.ELEM_FEL);
        
        // egy elemmel
        kodtar.addElem(e0);
        kodtar.elemMozgatas(e0, Kodtar.ELEM_FEL);
        assertTrue(helyesSorrend(kodtar, "e0"));
                               
        //teljesen mas elemmel probalkozva
        // nem tortenhet semmi
        kodtar.elemMozgatas(masElem, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e0"));
        
        //nullal
        // nem tortenhet semmi
        kodtar.elemMozgatas(null, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e0"));
    }
    
    /**
     * Teszteset az elemek mozgatasara.
     * 
     * Eloszor csak egyet leptet mindig, fel es le,
     * majd teszteli tobb lepessel is.
     * 
     */
    @Test
    public void mozgatasTest(){
        Kodtar kodtar = new Kodtar("k1", "k1m");
        final KodtarElem e0 = new KodtarElem("e0", "megnev0", kodtar.getKod());
        final KodtarElem e1 = new KodtarElem("e1", "megnev1", kodtar.getKod());
        final KodtarElem e2 = new KodtarElem("e2", "megnev2", kodtar.getKod());
        final KodtarElem e3 = new KodtarElem("e3", "megnev3", kodtar.getKod());                
        kodtar.addElem(e0);
        kodtar.addElem(e1);
        kodtar.addElem(e2);
        kodtar.addElem(e3);                                
        
        // nullas elem mozgatasa lefele
        kodtar.elemMozgatas(e0, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e1", "e0", "e2", "e3"));
        
        // nullas elem mozgatasa felfele
        kodtar.elemMozgatas(e0, Kodtar.ELEM_FEL);                
        assertTrue(helyesSorrend(kodtar, "e0", "e1", "e2", "e3"));
        
        // mégegyszer fel (átfordul!)
        kodtar.elemMozgatas(e0, Kodtar.ELEM_FEL);                
        assertTrue(helyesSorrend(kodtar, "e1", "e2", "e3", "e0"));
        
        // kettes elem lefele
        kodtar.elemMozgatas(e2, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e1", "e3", "e2", "e0"));
        
        // kettes elem lefele
        kodtar.elemMozgatas(e2, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e1", "e3", "e0", "e2"));
        
        // kettes elem lefele (átforudul)
        kodtar.elemMozgatas(e2, Kodtar.ELEM_LE);                
        assertTrue(helyesSorrend(kodtar, "e2", "e1", "e3", "e0"));
                
        // mozgatas tobb mint egy lepessel
        kodtar.elemMozgatas(e2, 2);                
        assertTrue(helyesSorrend(kodtar,  "e1", "e3", "e2", "e0"));
        kodtar.elemMozgatas(e2, -7);                
        assertTrue(helyesSorrend(kodtar,  "e1", "e3", "e0", "e2"));        
        kodtar.elemMozgatas(e2, 14);
        assertTrue(helyesSorrend(kodtar,  "e1", "e2", "e3", "e0"));
        kodtar.elemMozgatas(e0, -4);    // mozgatas ugyanoda
        assertTrue(helyesSorrend(kodtar,  "e1", "e2", "e3", "e0"));
    }
    
    /**
     * Elemek kulcs szerinti rendezésének tesztje.
     * 
     */
    @Test
    public void sortByKodTest(){
        Kodtar kodtar = new Kodtar("k1", "k1m");
        final KodtarElem e0 = new KodtarElem("e0", "megnev0", kodtar.getKod());
        final KodtarElem e1 = new KodtarElem("e1", "megnev1", kodtar.getKod());
        final KodtarElem e2 = new KodtarElem("e2", "megnev2", kodtar.getKod());
        final KodtarElem e3 = new KodtarElem("e3", "megnev3", kodtar.getKod());                
        kodtar.addElem(e3);  
        kodtar.addElem(e1);
        kodtar.addElem(e0);
        kodtar.addElem(e2);
        
        kodtar.sortElemekByKod();
        assertTrue(helyesSorrend(kodtar, "e0", "e1", "e2", "e3"));                
    }
    
    /**
     * Megviszgalja, hogy a kodtarban a kapott sorrendben
     * szerepelnek-e az elemek.
     * 
     * 
     * @param kodtar
     * @param expectedSorrend a vart sorrend, elem ID-ket kell megadni sorban
     * @return 
     */
    private boolean helyesSorrend(Kodtar kodtar, String... expectedSorrend){
        assertEquals("A vart sorrend tombben levo elemek szama nem egyezik a tenyleges elemszammal.", 
                kodtar.getKodtarElemek().size(), expectedSorrend.length);
        
        int i = 0;
        for(KodtarElem elem : kodtar.getKodtarElemek()){
            if (!elem.getId().equals(expectedSorrend[i])){
                return false;
            }
            i++;
        }
        return true;             
    }
    
    private Date getDate(int ev, int ho, int nap){
        Calendar c = GregorianCalendar.getInstance();
        c.set(ev, ho, nap);
        return c.getTime();
    }

    /**
     * Ellenorzi, hogy jelenleg dirty a kodtar,
     * majd ment, es megnezi, hogy mentes utan mar
     * NEM dirty.          
     * 
     * @param kodtar
     * @return a friss kodtar objektumot adja vissza
     */
    private Kodtar assertDirtyAndSave(Kodtar kodtar){
        assertTrue(kodtar.isDirty());        
        kodtar = kodtarService.save(kodtar);    // rafrissit
        assertFalse(kodtar.isDirty());   // mentes utan nem lehet dirty
        
        return kodtar;
    }
    
}
