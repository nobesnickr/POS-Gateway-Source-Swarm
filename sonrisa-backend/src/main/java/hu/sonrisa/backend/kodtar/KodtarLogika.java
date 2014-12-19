/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sonrisa.backend.kodtar;

import hu.sonrisa.backend.dao.JpaVersionException;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import hu.sonrisa.backend.model.util.DateUtil;
import hu.sonrisa.backend.model.util.StringUtil;
import hu.sonrisa.backend.org.apache.tools.zip.ZipEntry;
import hu.sonrisa.backend.org.apache.tools.zip.ZipOutputStream;
import hu.sonrisa.backend.versionedobject.AbstractVersionedObjectLogic;
import hu.sonrisa.backend.versionedobject.VersionedObjectLocator;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

/**
 *
 * @author joe
 * @author cserepj
 */
@Service
@Configurable
public class KodtarLogika extends AbstractVersionedObjectLogic<Kodtar, KodtarEntity> 
                                implements VersionedObjectLocator<Kodtar> {



    /**
     * 
     * @param dao
     */
    @Autowired
    public KodtarLogika(KodtarDao dao) {
        super(KodtarEntity.class, dao);
    }

    /**
     * Logikai torles.
     * 
     * @param kodtarEntityId 
     */
    public void delete(String kodtarEntityId) {
        KodtarEntity d = findEntity(kodtarEntityId);
        d.setDeleted(true);                
        cache.remove(d.getId());      
    }


    /**
     *
     * @param filter
     * @param start
     * @param end
     * @return
     */
    public List<Kodtar> findKodtar(JpaFilter<KodtarEntity> filter, int start, int end) {
        List<KodtarEntity> entities = findEntity(filter, start, end);
        List<Kodtar> result = new ArrayList<Kodtar>();
        for (KodtarEntity entity : entities) {
            result.add(unwrap(entity));
        }
        return result;
    }

    /**
     * Dimenziót keres kód alapján.
     *
     * @param kod 
     * @return 
     */
    public List<Kodtar> findKodtarByKod(String kod) {
        Collection<KodtarEntity> kodtarEntity = dao.find(
                SimpleFilter.of(KodtarEntity.class).addParameter("kod", kod), 0, 0);
        List<Kodtar> ret = new ArrayList<Kodtar>();
        for (KodtarEntity de : kodtarEntity) {
            Kodtar d = unwrap(de);
            ret.add(d);
        }
        return ret;
    }

    /**
     * Megszámolja, hogy hány olyan Dimenzió entitás található az adatbázisban,
     * ami megfelel a kapott filter paramétereknek.
     *
     * @param filter
     * @return
     */
    public long countKodtar(ProviderJpaFilter<KodtarEntity> filter) {
        if (filter == null) {
            return 0;
        }
        return count(filter);
    }
    
    /**
     * 
     * @param kod
     * @return
     */
    public Kodtar findLatestKodtar(String kod) {
        return findLatestVerzio(kod);
    }
    /**
     * 
     */
    public List<Kodtar> findAllLatestKodtar(Boolean deleted) {
        return unwrap(findAllLatest(deleted));
    }

    /**
     * 
     * @param uuid
     * @return
     */
    public String exportKodtarElemek(String uuid) {
        final String headerKodtar = "KODTAR"; 
        final String headerElemId = "ELEM_ID"; 
        final String headerElemMegn = "ELEM_MEGNEVEZES"; 
               
        Kodtar kodtar = find(uuid);
        if (kodtar == null) {
            return "";
        }

        //header-höz az column lista létrehozása
        List<String> columns = new ArrayList<String>(Arrays.asList(headerKodtar, headerElemId, headerElemMegn));
        for (KodtarElemAttrTipus attrTipus : kodtar.getAttributumok()) {
            columns.add(attrTipus.getNev());
        }

        //header kiírása        
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String col : columns) {
            if (!first) {
                sb.append(";");
            }
            sb.append(col);
            first = false;
        }
        sb.append(StringUtil.newLine());
       

        //dimenzioelemek kiírása
        Map<String, String> rowValues = new HashMap<String, String>();
        for (KodtarElem de : kodtar.getKodtarElemek()) {            
            // values map feltoltese ertekekkel
            rowValues.put(headerKodtar, de.getKodtar());
            rowValues.put(headerElemId, de.getId());
            rowValues.put(headerElemMegn, de.getMegnevezes());
            for (KodtarElemAttr attr : de.getAttributumLista()) {
                rowValues.put(attr.getNev(), attr.getErtek());
            }

            // egy sor kiirasa
            first = true;
            for (String col : columns) {
                if (!first) {
                    sb.append(";");
                }
                String value = rowValues.get(col);
                sb.append(value == null ? "" : value);
                first = false;
            }
            sb.append(StringUtil.newLine());
            
            // torolni kell a map-et, kulonben az elozo elem ertekei beragadhatnak, 
            // ha az aktualis dim elemen nincs minden attributum kitoltve
            rowValues.clear();
        }

        return sb.toString();
    }
    
    /**
     * 
     * @param toExportList
     * @param outStream
     * @throws IOException 
     */
    public void exportKodtarak(List<KodtarEntity> toExportList, OutputStream outStream) throws IOException {
        ZipOutputStream zos = null;      
        try{
            zos = new ZipOutputStream(outStream);
            zos.setEncoding(StringUtil.UTF8);
            for (KodtarEntity kodtarEntity : toExportList) {
                ZipEntry zipEntry = new ZipEntry(KodtarElemCSVImporter.kodtarToFileName(kodtarEntity));
                zos.putNextEntry(zipEntry);
                zos.write(exportKodtarElemek(kodtarEntity.getId()).getBytes(StringUtil.UTF8));
                zos.closeEntry();
            }
        }finally{
            if(zos != null){
                IOUtils.closeQuietly(zos);
            }
        }
    }

   /*  
     * Kodtar elemek import eredményének mentése a kodtarba.
     *
     * @param eredmeny import eredménye
     * @param userId
     * @param fullName
     * @return
     * @throws JpaVersionException  
     */
    public List<Kodtar> saveKodtarElemekImportEredmenye(KodtarElemImportEredmenye eredmeny,
            String userId, String fullName) throws JpaVersionException {
        final SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FULL_DATE_FORMAT);
        final String versionNameStr = "Import - " + sdf.format(new Date());
        
        if(eredmeny.isFromExportFile()){
            return saveImportaltKodtarkWithUUID(eredmeny, userId, fullName, versionNameStr);
        }else{
            return saveImportaltKodtarkSimple(eredmeny, userId, fullName, versionNameStr);
        }
    }
    /**
     * 
     * @param eredmeny
     * @param userId
     * @param fullName
     * @return 
     */
    private List<Kodtar> saveImportaltKodtarkSimple(KodtarElemImportEredmenye eredmeny, 
                                    String userId, String fullName, String versionNameStr) {
        // kodtar elem lista hozzáadása a kodtarhoz
        Map<String, Kodtar> map = new HashMap<String, Kodtar>();

//        boolean first = true;
        for (KodtarElem kodtarElem : eredmeny.getCelpont()) {
            Kodtar dim = map.get(kodtarElem.getKodtar());

            if (dim == null) {
                dim = find(kodtarElem.getKodtar());
                map.put(kodtarElem.getKodtar(), dim);
            }
            if (dim == null) {
                dim = new Kodtar(kodtarElem.getKodtar(), kodtarElem.getKodtar());
                map.put(kodtarElem.getKodtar(), dim);
            }
            KodtarElem oldKodtarElemWithSameID = dim.findElemById(kodtarElem.getId());
            if (oldKodtarElemWithSameID != null) {
                dim.removeElem(oldKodtarElemWithSameID);
            }
            dim.addElem(kodtarElem);
            //attributumok, fix hossz hozzaadasa
            updateKodtar(kodtarElem, dim, eredmeny/*, first*/);
//            first = false;
        }
        List<Kodtar> ret = new ArrayList<Kodtar>();
        for (Kodtar dim : map.values()) {
            if (dim.getVerzioNev() == null) {
                dim.setVerzioNev(versionNameStr);
            }
            ret.add(unwrap(save(dim, userId, fullName)));
        }
        return ret;
    }
    
    /**
     * 
     * @param eredmeny
     * @param userId
     * @param fullName
     * @return 
     */
    private List<Kodtar> saveImportaltKodtarkWithUUID(KodtarElemImportEredmenye eredmeny,
            String userId, String fullName, String versionNameStr){
        List<Kodtar> result = new ArrayList<Kodtar>();
        for (Kodtar kodtar : eredmeny.getImportaltKodtark()) {
            Kodtar kodtarDb = find(kodtar.getId());
            if(kodtarDb == null){//nincs ilyen uuid
                //ha letezik már ilyen dimenzioObjektum ilyen uuid-val
                //ilyenkor nyilván do nothing   
//                boolean first = true;
                for (KodtarElem elem: kodtar.getKodtarElemek()) {
                    updateKodtar(elem, kodtar, eredmeny/*, first*/);
//                    first = false;
                }
                kodtar.setVerzioNev(versionNameStr);
                KodtarEntity savedDim = save(kodtar, userId, fullName);
                result.add(savedDim.getWrappedObject());
            }
        }     
        return result;
    }
    
    private void updateKodtar(KodtarElem elem, Kodtar kodtar, KodtarElemImportEredmenye eredmeny/*, boolean first*/){
        
            // kodtar elem attribútumok egyeztetése
            // ha nincs a kodtaron olyan attribútum, és eddig nem is vettük fel,
            // ami az elemen van, akkor rárakja a kodtarra
            for (KodtarElemAttr attr : elem.getAttributumLista()) {
                KodtarElemAttrTipus attrTipus = kodtar.findElemAttrTipusByName(attr.getNev());
                if (attrTipus == null) {
                    // nincs admin változás, ezért egyszerűen hozzáadja
                    KodtarElemAttrTipus ujAttrTipus = 
                            new KodtarElemAttrTipus(attr.getNev(), eredmeny.getKodtar(attr.getNev()));
                    kodtar.addAttributum(ujAttrTipus);
                }
            }

//            //Beállítjuk a hosszát az elemek alapján, ha még nem volt beállítva
//            if (kodtar.isFixHosszuElemId()) {
//                if (kodtar.getFixIdHossz() != elem.getKodtarElemId().length()) {
//                    kodtar.setFixIdHossz(0);                   
//                }
//            } else if (first) {
//                kodtar.setFixIdHossz(elem.getKodtarElemId().length());                                
//            }
    }
    
}
