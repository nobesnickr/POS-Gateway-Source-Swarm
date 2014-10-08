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

import hu.sonrisa.backend.dao.JpaVersionException;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.versionedobject.VersionedObjectLocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 *
 * @author Joe
 */
public interface KodtarService extends VersionedObjectLocator<Kodtar> {        

    /**
     * Az id által meghatározott kódtár entity visszaadása.
     * @param uuid
     * @return
     */
     KodtarEntity findEntity(String uuid);

    /**
     * Az id által meghatározott kódtár objektum visszaadása.
     * @param uuid
     * @return
     */     
     @Override
     Kodtar find(String uuid);

    /**
     *
     * @param filter
     * @param start
     * @param end
     * @return
     */
     List<KodtarEntity> findEntities(JpaFilter<KodtarEntity> filter, int start, int end);

    
    /**
     * Egy új kódtár mentése.
     * 
     * @param ad
     * @return 
     */
     Kodtar save(Kodtar kodtar);

    /**
     * Visszaadja azokat az aktív kódtárakat, amiknek a kódja az átadott string.
     * 
     * @param kod
     * @return 
     */
     List<Kodtar> findByKod(String kod);

    /**
     * Egy adott kódtár verzió (logikai) törlése.
     * A státuszát állítja inaktív-ra.
     *
     * @param uuid a kódtár verzió egyedi azonosítója
     */
     void delete(String uuid);

    /**
     * Visszaad minden kódtárat.
     * <strong>A logikailag torolteket is!</strong>
     * 
     * @return 
     */
     List<Kodtar> findAll();
     
     /**
      * Visszaad minden aktiv kodtarat.
      * <p/>
      * Azaz a logikailag torolteket nem.
      * 
      * @return 
      */
     List<Kodtar> findAllAktiv();
   
    /**
     * Visszaadja a megadott kódú kódtár utoljára mentett aktív verzióját.
     * 
     * @param kod
     * @return 
     */
     Kodtar findLatestByKod(String kod);

    /**
     * Visszaadja a kódtárak verziói közül a legutolsókat.
     * <p/>
     * Utolsó alatt NEM az érvényesség kezdete-vége dátumok által meghatározott
     * legkésőbbit kell érteni, hanem a kódtárak egyes verziói közül az utolsót.
     * (Azaz azt a verziót, ami utoljára lett létrehozva.)
     *
     * @param deleted a törölt, a létezők, vagy mindkettő között keressen-e
     * <ul>
     *  <li> true: a törölt kódtárak közül adja vissza az utolsót </li>
     *  <li> false: a létező kódtárak közül adja vissza az utolsót </li>
     *  <li> null: nem veszi figyelembe, hogy a kódtár létezik-e, vagy már törölték </li>
     * </ul>
     * @return
     */
     List<KodtarEntity> findAllLatestEntity(Boolean deleted);

    /**
     * Minden egyes kódtár legutolsó verzióját tartalmazó
     * listát ad vissza.
     * <p/>
     * Utolsó alatt NEM az érvényesség kezdete-vége dátumok által meghatározott
     * legkésőbbit kell érteni, hanem a kódtárak egyes verziói közül az utolsót.
     * (Azaz azt a verziót, ami utoljára lett létrehozva.)
     * 
     * @param deleted a törölt, a létezők, vagy mindkettő között keressen-e
     * <ul>
     *  <li> true: a törölt kódtárak közül adja vissza az utolsót </li>
     *  <li> false: a létező kódtárak közül adja vissza az utolsót </li>
     *  <li> null: nem veszi figyelembe, hogy a kódtár létezik-e, vagy már törölték </li>
     * </ul>
     * @return 
     */
     List<Kodtar> findAllLatest(Boolean deleted);

    /**
     * Listát ad vissza az azonos kódú kódtárak entitás objektumaival.
     * @param kod
     * @param deleted Csak a törölteket adja-e vissza. Ha null akkor mindet, ha true akkor csak az archive.
     * @return
     */
     List<KodtarEntity> findEntitiesByKod(String kod, Boolean deleted);

    /**
     * Kiexportálja az adott kódtár verzió elemeit CSV formátumba (UTF-8 kódolással).
     * @param uuid A kódtár verziójának azonosítója.
     * @return A kiexportált kódtár elemek CSV formátumban (UTF-8).
     */
    String exportKodtarElemek(String uuid);
    
    /**
     * Kodtarak listajat lehet exportalni az atadott outputstreamre
     * 
     * @param toExportList ezeket a kódtárakat exportaljuk ki
     * @param responseOutputStream - az outputstreamet egy zip streambe rakja, kodtarankent lesz egy darab csv fajl
     * 
     * @throws IOException - finally-ben megpróbáljuk lezárni az outputstream-et
     */
    void exportKodtarak(List<KodtarEntity> toExportList, OutputStream outStream) throws IOException;
    
    /**
     * Kodtar elemeket importál be CSV fájlból. A kódtárelemek típusa az
     * általános {@link KodtarElem} osztály lesz.
     *
     * @param input
     * @param charset
     * @return
     */
    KodtarElemImportEredmenye importKodtarElemek(InputStream input, Charset charset);

    /**
     * Kodtar elemek import eredményének mentése.
     *
     * @param eredmeny import eredménye
     * @return
     * @throws JpaVersionException
     */
    List<Kodtar> saveKodtarElemekImportEredmenye(KodtarElemImportEredmenye eredmeny) throws JpaVersionException;

}
