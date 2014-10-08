/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.JpaVersionException;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import hu.sonrisa.backend.entity.VersionedEntity;
import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import hu.sonrisa.backend.model.MuveletStatusz;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Teszt osztálya {@link KodtarServiceImp}-re.
 *
 * @author Joe
 */
public class KodtarServiceImplDbTest extends BackendDbTestBase {

    //private static final Logger LOGGER = LoggerFactory.getLogger(KodtarServiceImplDbTest.class);
    //teszt fajlban levo string egyezzen ezzel
    private static final String TESZT_SZAKFELADAT = "szakfeladat";
    /**
     * Teszt fájl dimenzio elem importáláshoz.
     * Olyan dimenzió elemek vannak benne, amiken vannak plusz atrribútumok.
     * KodtarElemek_attributumTest.zip
     */
    public static final String DIMENZIO_ELEMEK_ATTR_TESZT = "KodtarElemek_attributumTest.zip";    
    /**
     * Teszt fájl dimenzio elem importáláshoz.
     * KodtarElemek_Szakfeladat.zip
     */
    public static final String DIMENZIO_ELEMEK_SZAKFELADAT = "KodtarElemek_Szakfeladat.zip";

            
    @Autowired
    private KodtarService kodtarService;

    @Autowired
    private KodtarDao kodtarDao;

    /**
     * 
     */
    @Before
    public void setUp(){
        //clearing
        removeAll(kodtarDao);
    }

    /**
     * 
     */
    @After
    public void tearDown(){
        //clearing
        removeAll(kodtarDao);
    }

    /**
     * Test of findKodtarEntityById method, of class KodtarServiceImpl.
     * @throws JpaVersionException 
     */
    @Test
    public void testFindKodtarEntityById() throws JpaVersionException {
        // uj entitás létrehozása        
        final String KOD = "test-kod";
        final String MEGNEVEZES = "test-megnevezes";
        Kodtar dimenzio = createKodtar(KOD, MEGNEVEZES);
        kodtarService.save(dimenzio);

        // findById
        KodtarEntity result = kodtarService.findEntity(dimenzio.getPersistenceID());

        // ellenőrzés
        assertNotNull(result);
        assertEquals(KOD, result.getKod());
        assertEquals(MEGNEVEZES, result.getMegnevezes());
    }
    
    /**
     * 
     */
    @Test
    public void testDeleteKodtar(){
        // uj entitás létrehozása                
        Kodtar kodtar = createKodtar("kod", "megnevezes");
        kodtarService.save(kodtar);        
        // megvan es nem torolt
        assertFalse(kodtarService.findEntity(kodtar.getId()).isDeleted());
        assertFalse(kodtarService.find(kodtar.getId()).isDeleted());
        // torles
        kodtarService.delete(kodtar.getId());        
        // torolt
        assertTrue(kodtarService.findEntity(kodtar.getId()).isDeleted());
        assertTrue(kodtarService.find(kodtar.getId()).isDeleted());
    }
    
    @Test
    public void testOfBasicFinders(){
        // ket lap kodtar
        Kodtar kodtar1 = createKodtar("kod1", "megnevezes1");
        kodtarService.save(kodtar1);
        final String kod2 = "kod2";
        Kodtar kodtar2 = createKodtar(kod2, "megnevezes2");
        kodtarService.save(kodtar2);
        // egy leszarmazott kodtar
        Kodtar kodtar2v2 = createKodtar(kod2, "megnevezes2v2");
        kodtar2v2.setSzuloId(kodtar2.getId());
        kodtarService.save(kodtar2v2);
        // egy torolt kodtar
        Kodtar kodtar3 = createKodtar("kod3", "megnevezes3");
        kodtarService.save(kodtar3);
        kodtarService.delete(kodtar3.getId());
        
        assertEquals(4, kodtarService.findAll().size());
        assertEquals(3, kodtarService.findAllAktiv().size());    // 3 aktiv kodtarat hoztunk letre
        assertEquals(3, kodtarService.findAllLatest(null).size()); 
        final boolean deleted = true; 
        assertEquals(1, kodtarService.findAllLatest(deleted).size()); 
        assertEquals(2, kodtarService.findAllLatest(!deleted).size()); 
        assertEquals(2, kodtarService.findAllLatestEntity(!deleted).size()); 
        assertEquals(2, kodtarService.findByKod(kod2).size()); 
        assertEquals(2, kodtarService.findEntitiesByKod(kod2, !deleted).size()); 
        assertEquals(kodtar2v2.getId(), kodtarService.findLatestByKod(kod2).getId()); 
    }

    /**
     * Test of findKodtar method, of class KodtarServiceImpl.
     *
     * Teszteli a keresést megnevezés töredékre.
     * @throws JpaVersionException 
     */
    @Test
    public void testFindKodtar() throws JpaVersionException {
        // uj entitás létrehozása
        final String KOD = "test-kod";
        final String MEGNEVEZES = "test-megnevezes";
        Kodtar dimenzio = createKodtar(KOD, MEGNEVEZES);
        kodtarService.save(dimenzio);

        // egy másik entitás
        Kodtar dimenzio2 = createKodtar("masik-kod", "nem-erre-keresunk-ra");
        kodtarService.save(dimenzio2);

        // findByFilter (megnevezés egy töredékére)
        SimpleFilter<KodtarEntity> filter = new SimpleFilter(KodtarEntity.class, 
                new FilterParameter("megnevezes", "%"+MEGNEVEZES.substring(0, 3)+"%", " LIKE "));
        List<KodtarEntity> result = kodtarService.findEntities(filter, 0, 0);

        // ellenőrzés
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(KOD, result.get(0).getKod());
        assertEquals(MEGNEVEZES, result.get(0).getMegnevezes());
    }

    /**
     * Beimportál dimenzió elemeket egy dimenzióhoz, majd menti az import eredményét.
     * A teszteléshez a {@link Szakfeladat} dimenzió elem típust fogjuk használni,
     * de lehetne mást is.
     *
     * @throws JpaVersionException
     */
    @Test
    public void testKodtarElemekCSVImport() throws JpaVersionException {
        // az importnál használt teszt fájl alapján a várt eredmények
        final int VART_ELEMEK_SZAMA = 3;
        final KodtarElem VART_ELEM_1 = createSzakfeladat("0001", "szakfeladat1");
        final KodtarElem VART_ELEM_2 = createSzakfeladat("0002", "szakfeladat2");
        final KodtarElem VART_ELEM_3 = createSzakfeladat("0003", "szakfeladat3");

        // dimenzió létrehozása, amihez importálni fogjuk az elemeket
        final String DIM_KOD = TESZT_SZAKFELADAT;
        Kodtar dim = createKodtar(DIM_KOD, "teszt-szakfeladat-dimenzio");
        kodtarService.save(dim);

        // elemek importja
        InputStream is = KodtarServiceImplDbTest.class.getResourceAsStream(DIMENZIO_ELEMEK_SZAKFELADAT);
        KodtarElemImportEredmenye eredmeny = kodtarService.importKodtarElemek(is, Charset.defaultCharset());

        assertEquals(MuveletStatusz.SIKERES, eredmeny.getStatusz());

        List<Kodtar> importedKodtark = kodtarService.saveKodtarElemekImportEredmenye(eredmeny);
        assertNotNull(importedKodtark);
        assertEquals("Ennyinek kene lennie, mert csak egyfele dimenzio volt az importalt csv-ben.", 1, importedKodtark.size());
        Kodtar importedDim = importedKodtark.get(0);

        // ellenőrzés 
        // a korábban létező dim verzió változatlan maradt 
        dim = kodtarService.find(dim.getPersistenceID());
        assertNotNull(dim);
        assertNotNull(dim.getKodtarElemek());
        assertEquals(0, dim.getKodtarElemek().size());

        // az import során létrejött verzió viszont tartalmazza az elemeket is 
 	Kodtar dimuj = kodtarService.find(importedDim.getId()); 
        assertNotNull(dimuj);
        assertNotNull(dimuj.getKodtarElemek());
        assertEquals(VART_ELEMEK_SZAMA, dimuj.getKodtarElemek().size());

        boolean egyMegvan = false;
        boolean kettoMegvan = false;
        boolean haromMegvan = false;
        for (KodtarElem szakfeladat : dimuj.getKodtarElemek()) {
            if (ketKodtarElemEgyezik(VART_ELEM_1, szakfeladat)) {
                egyMegvan = true;
            }
            if (ketKodtarElemEgyezik(VART_ELEM_2, szakfeladat)) {
                kettoMegvan = true;
            }
            if (ketKodtarElemEgyezik(VART_ELEM_3, szakfeladat)) {
                haromMegvan = true;
            }
        }
        assertTrue("Hiányzik az egyik dimenzió elem.", egyMegvan);
        assertTrue("Hiányzik az egyik dimenzió elem.", kettoMegvan);
        assertTrue("Hiányzik az egyik dimenzió elem.", haromMegvan);
    }

    /**
     * Azt ellenőrzi a teszteset, hogy a dimenzió elemek plusz attribútumai is
     * beimportálódnak-e. Azt is ellenőrzi, hogy a dimenzióra rákerültek-e az
     * attribútum típusok.
     * <p/>
     *
     * Ebben a tesztesetben a dimenzió még nem létezik az importkor.
     *
     *
     * @throws JpaVersionException
     */
    @Test
    public void testElemekCSVImportAttrTestWithoutDim() throws JpaVersionException {
        // azért ez a kód, mert a beimportált CSV fájlban ez van
        final String dimKod = "testDim";
        final String ATTR1_NAME = "ATTR1";
        final String ATTR2_NAME = "ATTR2";
        dimenzioElemAttrImportTest(dimKod, ATTR1_NAME, ATTR2_NAME);
    }

    /**
     * Azt ellenőrzi a teszteset, hogy a dimenzió elemek plusz attribútumai is beimportálódnak-e.
     * Azt is ellenőrzi, hogy a dimenzióra rákerültek-e az attribútum típusok.
     * <p/>
     *
     * Ebben a tesztesetben a dimenzió már létezik az importkor, és az egyik attribútum rajta is van.
     *
     *
     * @throws JpaVersionException
     */
    @Test
    public void testElemekCSVImportAttrTestWithDim() throws JpaVersionException {
        // azért ez a kód, mert a beimportált CSV fájlban ez van
        final String kod = "testDim";
        final String ATTR1_NAME = "ATTR1";
        final String ATTR2_NAME = "ATTR2";

        // létrehozza dimenziót előre
        Kodtar kodtar = createKodtar(kod, kod);
        KodtarElemAttrTipus attr1 = new KodtarElemAttrTipus(ATTR1_NAME, null);
        kodtar.addAttributum(attr1);
        kodtarService.save(kodtar);

        dimenzioElemAttrImportTest(kodtar.getKod(), attr1.getNev(), ATTR2_NAME);
    }

    /**
     * Teszt a dimenzió elem módosításra.
     * Létrehoz egy dimenziót 3 elemmel. Az egyik elemet módosítja, majd ellenőrzi, hogy
     * valóban megváltozott-e, illetve, hogy a többi elem nem változott.
     * @throws JpaVersionException 
     */
    @Test
    public void testUpdateKodtarElem() throws JpaVersionException {
        // tesztadat létrehozása
        Kodtar dimenzio = createKodtar("dim-kod", "megnevezes");
        KodtarElem dimElem1 = new KodtarElem("dimElem1", "Dimenzió elem 1", dimenzio.getKod());
        KodtarElem dimElem2 = new KodtarElem("dimElem2", "Dimenzió elem 2", dimenzio.getKod());
        KodtarElem dimElem3 = new KodtarElem("dimElem3", "Dimenzió elem 3", dimenzio.getKod());
        dimenzio.addElem(dimElem1);
        dimenzio.addElem(dimElem2);
        dimenzio.addElem(dimElem3);
        kodtarService.save(dimenzio);

        // ellenőrzés
        dimenzio = kodtarService.find(dimenzio.getPersistenceID());
        assertNotNull(dimenzio);
        Collection<KodtarElem> elemek = dimenzio.getKodtarElemek();
        boolean nemModositottMegvan1 = false;
        boolean nemModositottMegvan2 = false;
        boolean modositottMegvan = false;

        for (KodtarElem elemIter : elemek) {
            // a nem módosítottak megvannak
            if (ketKodtarElemEgyezik(dimElem1, elemIter)) {
                nemModositottMegvan1 = true;
            }
            // a nem módosítottak megvannak
            if (ketKodtarElemEgyezik(dimElem3, elemIter)) {
                nemModositottMegvan2 = true;
            }
            // a módosított is megvan
            if (ketKodtarElemEgyezik(dimElem2, elemIter)) {
                modositottMegvan = true;
            }
        }
        assertTrue(nemModositottMegvan1);
        assertTrue(nemModositottMegvan2);
        assertTrue(modositottMegvan);
    }

    /**
     * Teszteset a {@link KodtarService#findAllLatest()} metódusra.
     * Létrehoz pár dimenziót, mindegyikből 2 verziót, majd lekéri a legutolsó verziókat.
     *
     * @throws JpaVersionException
     */
    @Test
    public void testFindAllLatest() throws JpaVersionException {
        System.out.println("testFindAllLatest");
        final int DARABSZAM = 5;

        // uj dimenzió entitások létrehozása az adatbázisban
        List<Kodtar> dimenziok = new ArrayList<Kodtar>();
        for (int i = 0; i < DARABSZAM; i++) {
            Kodtar dim = createKodtar("kod-" + i, "dim-megnevezes-" + i);
            dimenziok.add(dim);
            kodtarService.save(dim);
        }

        //módosítás, új verzió mentése
        final String UJ_MEGNEVEZES_POSTFIX = " --- uj verzio";
        for (Kodtar dim : dimenziok) {
            dim.setMegnevezes(dim.getMegnevezes() + UJ_MEGNEVEZES_POSTFIX);
            kodtarService.save(dim);
        }

        // legutolsó verziók lekérése
        List<KodtarEntity> latestVersions = kodtarService.findAllLatestEntity(false);

        // ellenőrzés
        assertEquals(dimenziok.size(), latestVersions.size());
        for (KodtarEntity dimEntity : latestVersions) {
            // ellenőrzi, hogy tényleg a későbbi verzió jött-e viszza
            assertTrue(dimEntity.getMegnevezes().contains(UJ_MEGNEVEZES_POSTFIX));
        }
    }

    @Test
    public void testExport()throws IOException{
        Kodtar dimenzio = createKodtar("dim-kod", "megnevezes");
        KodtarElem dimElem1 = new KodtarElem("dimElem1", "Dimenzió elem 1", dimenzio.getKod());
        KodtarElem dimElem2 = new KodtarElem("dimElem2", "Dimenzió elem 2", dimenzio.getKod());
        KodtarElem dimElem3 = new KodtarElem("dimElem3", "Dimenzió elem 3", dimenzio.getKod());
        dimenzio.addElem(dimElem1);
        dimenzio.addElem(dimElem2);
        dimenzio.addElem(dimElem3);
        dimenzio = kodtarService.save(dimenzio);
        KodtarEntity diment = kodtarService.findEntity(dimenzio.getId());
        assertNotNull(diment);
        List<KodtarEntity> lista = new ArrayList<KodtarEntity>();
        lista.add(diment);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
       kodtarService.exportKodtarak(lista, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        KodtarElemImportEredmenye die = kodtarService.importKodtarElemek(in,  Charset.defaultCharset());
        assertEquals(1, die.getImportaltKodtark().size());
        assertEquals(dimenzio.getId(), die.getImportaltKodtark().get(0).getId());        
    }
   
    /**
     * Teszteset a dimenzió elemek CSV exportjára.
     * Kapcsolódó ticket: #4755 (az export során a korábbi elemek attribútumai beragadtak)
     * 
     * Teszt forgatókönyv:
     *  - csinál egy dimenziót, amin van két attribútum
     *  - felvesz egy elemet, amin ki vannak tölve az attribútumok
     *  - felvesz egy elemet, amin nincsenek kitölve az attribútumok
     *  - felvesz egy elemet, amin ki vannak tölve az attribútumok
     *  
     *  - készít egy exportot
     *  - majd összehasonlítja az elvárt eredménnyel
     */
    @Test
    public void exportKodtarElemekTest() throws IOException{
        
        final String attr1Name = "attr1";
        final String attr2Name = "attr2";
        
        // dimenziót létrehoz
        Kodtar dim = createKodtar("dimenzio-attributummal-kod", "dimenzio-attributummal");
        dim.addAttributum(new KodtarElemAttrTipus(attr1Name, ""));
        dim.addAttributum(new KodtarElemAttrTipus(attr2Name, ""));
        
        // elem1 attribútumokkal
        KodtarElem elem1 = new KodtarElem("elem1", "elem1", dim.getKod());
        elem1.attributum(attr1Name, "attr1Ertek");
        elem1.attributum(attr2Name, "attr2Ertek");
        
        // elem2, attribútumok nélkül
        KodtarElem elem2 = new KodtarElem("elem2", "elem2", dim.getKod());
        
        // elem3, attribútumokkal
        KodtarElem elem3 = new KodtarElem("elem3", "elem3", dim.getKod());
        elem3.attributum(attr1Name, "egy");
        elem3.attributum(attr2Name, "ketto");
        
        // elemeket hozzáad, dimenziót ment
        dim.addElem(elem1, elem2, elem3);        
        dim = kodtarService.save(dim);
        
        // export
        final String exportCsv = kodtarService.exportKodtarElemek(dim.getPersistenceID());
        
        // ellenorzes
        assertNotNull(exportCsv);
        final String[] expectedResult = {
            "KODTAR;ELEM_ID;ELEM_MEGNEVEZES;attr1;attr2",
            "dimenzio-attributummal-kod;elem1;elem1;attr1Ertek;attr2Ertek",
            "dimenzio-attributummal-kod;elem2;elem2;;",
            "dimenzio-attributummal-kod;elem3;elem3;egy;ketto"};
        
        BufferedReader reader = new BufferedReader(new StringReader(exportCsv));
        String line;
        int cnt = 0;
        while((line = reader.readLine()) != null){
            assertNotNull(line);
            assertTrue("Több sor van az exportban, mint a várt eredményben", cnt < expectedResult.length);
            assertEquals("Ez a sor nem egyezik: " + cnt, expectedResult[cnt], line);
            cnt++;            
        }
    }
    
    // ---------------------------------------------------------------------
    // ~ Private methods
    // ---------------------------------------------------------------------
    /**
     * Készít egy dimenzió objektumot.
     * Nem ment adatbázisba.
     */
    private Kodtar createKodtar(String kod, String megnevezes) {
        return new Kodtar(kod, megnevezes);
    }

    /**
     * Készít egy {@link Szakfeladat} objektumot.
     * Nem ment adatbázisba.
     * @return
     */
    private KodtarElem createSzakfeladat(String kod, String nev) {
        return new KodtarElem(kod, nev, TESZT_SZAKFELADAT);
    }

    /**
     * Ellenőrzi, hogy két dimenzió elem minden property-ben megegyezik-e.
     * (elem id, megnevezes, parentId, dimenzio tipus)
     *
     * @return
     */
    private boolean ketKodtarElemEgyezik(KodtarElem expected, KodtarElem result) {
        return (stringPropertyEgyezik(expected.getId(), result.getId())
                && stringPropertyEgyezik(expected.getMegnevezes(), result.getMegnevezes())
                && stringPropertyEgyezik(expected.getKodtar(), result.getKodtar()));
    }

    /**
     * Két string megegyezik.
     *
     * @param expected
     * @param result
     * @return
     */
    private boolean stringPropertyEgyezik(String expected, String result) {
        if (expected == null && result == null) {
            return true;
        }
        return expected.equals(result);
    }

    /**
     * Két teszteset közös része, ami dimenzió elmek importját teszteli,
     * különös tekintettel a plusz attribútumokra.
     */
    private void dimenzioElemAttrImportTest(final String dimKod,
            final String attr1Name, final String attr2Name) throws JpaVersionException {
        InputStream is = KodtarServiceImplDbTest.class.getResourceAsStream(DIMENZIO_ELEMEK_ATTR_TESZT);
        KodtarElemImportEredmenye eredmeny = kodtarService.importKodtarElemek( is,  Charset.defaultCharset());

        assertEquals(MuveletStatusz.SIKERES, eredmeny.getStatusz());

        // import eredményének mentése
        kodtarService.saveKodtarElemekImportEredmenye(eredmeny);

        // dimenzió ellenőrzése        
        Kodtar dim = kodtarService.findLatestByKod(dimKod);
        assertNotNull(dim);
        assertNotNull(dim.getKodtarElemek());
        // azért ennyi, mert az importált CSV fájlban ennyi elem van
        assertEquals(4, dim.getKodtarElemek().size());
        assertNotNull(dim.getAttributumok());
        // azért ennyi, mert a importált CSV fájlban ennyi plusz attribútum van az elemeken
        assertEquals(2, dim.getAttributumok().size());
        List<KodtarElemAttrTipus> attrs = new ArrayList<KodtarElemAttrTipus>(dim.getAttributumok());
        for (KodtarElemAttrTipus attr : attrs) {
            final String attrName = attr.getNev();
            assertTrue(attrName.equals(attr1Name) || attrName.equals(attr2Name));
        }

        // elemek ellenőrzése
        // ellenőrzi, hogy azokon az elemeken van attribútum, amiken a CSV fájlban is van
        // és hogy az értékük stimmel-e
        for (KodtarElem elem : dim.getKodtarElemek()) {
            //ezen az elemen mindkét attribútum ki van töltve
            if ("001".equals(elem.getId())) {
                assertEquals(2, elem.getAttributumLista().size());
                assertEquals("attr1_value1", elem.findAttributum(attr1Name).getErtek());
                assertEquals("attr2_value1", elem.findAttributum(attr2Name).getErtek());
            }
            //ezen az elemen egyik attribútum sincs kitöltve
            if ("002".equals(elem.getId())) {
                assertEquals(2, elem.getAttributumLista().size());
            }
            //ezen az egyik attribútum van kitöltve
            if ("003".equals(elem.getId())) {
                assertEquals(2, elem.getAttributumLista().size());
                assertEquals("attr1_value3", elem.findAttributum(attr1Name).getErtek());
            }
            //ezen az egyik attribútum van kitöltve
            if ("004".equals(elem.getId())) {
                assertEquals(2, elem.getAttributumLista().size());
                assertEquals("attr2_value4", elem.findAttributum(attr2Name).getErtek());
            }
        }
    }

    /**
     * 
     * @param <U>
     * @param <T>
     * @param dao
     */
    protected <U extends Serializable, T extends VersionedEntity<U>> void removeAll(BaseJpaDao<U, T> dao) {
        Collection<T> defs = dao.findAll();
        for (T d: defs) {
            //mivel a verziozottObjektumokat nem torli a dao, csak setDeletedre allit, ez nekunk nem eleg
            dao.getEntityManager().remove(d);
        }
    }
}
