/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sonrisa.backend.kodtar;

import hu.sonrisa.backend.auth.Felhasznalo;
import hu.sonrisa.backend.auth.SessionCredentialsProvider;
import hu.sonrisa.backend.dao.JpaVersionException;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.model.UzenetTipus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author joe
 */
@Service("kodtarService")
@Configurable
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class KodtarServiceImpl implements KodtarService {

    @Autowired
    private KodtarLogika kodtarLogika;
    @Autowired
    private SessionCredentialsProvider sessionCredentialsProvider;

    // -----------------------------------------------------------------------
    // ~ Data manipulation methods
    // -----------------------------------------------------------------------
    /**
     * {@inheritDoc }
     *
     * @param ad
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Kodtar save(Kodtar ad) {
        KodtarEntity ent = kodtarLogika.save(ad,
                sessionCredentialsProvider.getSessionFelhasznalo().getId(),
                sessionCredentialsProvider.getSessionFelhasznalo().getNev());
        return find(ent.getId());
    }

    /**
     * {@inheritDoc }
     *
     * @param kodtarEntityId
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(String kodtarEntityId) {
//        final Felhasznalo felh = sessionCredentialsProvider.getSessionFelhasznalo();
        kodtarLogika.delete(kodtarEntityId);
    }

    // -----------------------------------------------------------------------
    // ~ Finder methods
    // -----------------------------------------------------------------------
    @Override
    public KodtarEntity findEntity(String id) {
        return kodtarLogika.findEntity(id);
    }

    @Override
    public Kodtar find(String id) {
        return kodtarLogika.find(id);
    }

    @Override
    public List<KodtarEntity> findEntities(JpaFilter<KodtarEntity> filter, int start, int end) {
        return kodtarLogika.findEntity(filter, start, end);
    }

    /**
     * Kodtar megkeresése név alapján
     *
     * @param nev
     * @return
     */
    @Override
    public List<Kodtar> findByKod(String nev) {
        return kodtarLogika.findKodtarByKod(nev);
    }

    /**
     * @return
     */
    @Override
    public List<Kodtar> findAll() {
        return kodtarLogika.findAll();                
    }

    /**
     * 
     * @return 
     */
    @Override
    public List<Kodtar> findAllAktiv() {
        return kodtarLogika.findAllAktiv();
    }
    
    

    @Override
    public Kodtar findLatestByKod(String kod) {
        return kodtarLogika.findLatestKodtar(kod);
    }

    @Override
    public List<KodtarEntity> findAllLatestEntity(Boolean deleted) {
        return kodtarLogika.findAllLatest(deleted);
    }

    /**
     * @param deleted
     * @return
     */
    @Override
    public List<Kodtar> findAllLatest(Boolean deleted) {
        return kodtarLogika.findAllLatestKodtar(deleted);
    }

    @Override
    public List<KodtarEntity> findEntitiesByKod(String kod, Boolean deleted) {
        return kodtarLogika.findVerziokByKod(kod, deleted);
    }

    @Override
    public String exportKodtarElemek(String uuid) {
        return kodtarLogika.exportKodtarElemek(uuid);
    }
    
    @Override
    public void exportKodtarak(List<KodtarEntity> toExportList, OutputStream outStream) throws IOException {
        kodtarLogika.exportKodtarak(toExportList, outStream);
    }
    
    /**
     * {@inheritDoc }
     *
     * @param strategia
     * @param input     inputStream
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public KodtarElemImportEredmenye importKodtarElemek(InputStream input, Charset charset) {
        KodtarElemCSVImporter importer = new KodtarElemCSVImporter();
        KodtarElemImportEredmenye eredmeny = importer.importKodtarElemek(input, charset);
        if(eredmeny.isFromExportFile()){
            for (Kodtar kodtar : eredmeny.getImportaltKodtark()) {
                Kodtar kodtarDb = find(kodtar.getId());
                if(kodtarDb != null){
                    eredmeny.addUzenet(UzenetTipus.FIGYELMEZTETES, 
                            "absztraktEredmeny.figyelmeztetes.kodtar.marLetezik", 
                            new Object[]{kodtarDb.getMegnevezes(), kodtarDb.getKod(), kodtarDb.getId()});
                            
                }
            }
        }
        return eredmeny;
    }

    /**
     * {@inheritDoc }
     *
     * @param eredmeny import eredménye
     * @return 
     * @throws JpaVersionException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<Kodtar> saveKodtarElemekImportEredmenye(KodtarElemImportEredmenye eredmeny) throws JpaVersionException {
        Felhasznalo f = sessionCredentialsProvider.getSessionFelhasznalo();
        return kodtarLogika.saveKodtarElemekImportEredmenye(eredmeny,
                f == null ? "" : f.getId(),
                f == null ? "" : f.getNev());
    }
}
