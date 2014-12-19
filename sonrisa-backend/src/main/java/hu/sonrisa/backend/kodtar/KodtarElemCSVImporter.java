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

import hu.sonrisa.backend.async.FileTipus;
import hu.sonrisa.backend.csv.ImporterHelper;
import hu.sonrisa.backend.model.MuveletStatusz;
import hu.sonrisa.backend.model.UzenetTipus;
import hu.sonrisa.backend.model.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 * Kodtar elemeket CSV fájlból felolvasó importer.
 *
 * @author Joe
 */
public class KodtarElemCSVImporter {

    /**
     * A beolvasott fajlok neveben ezzel vannak elvalasztva a kovetkezok:
     * 1. kodtar megnevezés
     * 2. kodtar kód
     * 3. uuid
     * 4. created at time in long ms
     * 
     * Lsd: {@link #resolveFileNameToKodtar(java.lang.String) }
     * es {@link #kodtarToFileName(hu.sonrisa.backend.kodtar.Kodtar) }
     * 
     */
    public static final String PATTERN_CHAR = "___";

    /**
     * Ennyi oszlopnak legalább lennie kell a felolvasott CSV fájlban, mert
     * az első oszlopban mindenképp a kodtar elem ID-nak kell lennie, a másodikban a
     * megnevezésnek.
     */
    private static final int KOTELEZO_OSZLOPOK_SZAMA = 3;
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KodtarElemCSVImporter.class);
    /**
     * Az eredmény objekum
     */
    private final KodtarElemImportEredmenye eredmeny = new KodtarElemImportEredmenye();

    /**
     * {@inheritDoc }
     * <p/>
     * 
     * A bemeneti fájlnak .csv kiterjesztésűnek kell lennie.
     * Az első oszlopban mindenképp a kodtar elem ID-nak kell lennie, a másodikban a
     * megnevezésnek. Utána egyéb attribútumok még lehetnek.
     *
     * @param kodtarElemFile
     * @return
     */
    public KodtarElemImportEredmenye importKodtarElemek(InputStream kodtarElemFile, Charset charset) {
        ZipInputStream zin = ImporterHelper.convertToZipStream(kodtarElemFile);
        List<KodtarElem> list = new ArrayList<KodtarElem>();
        ZipEntry entry = null;
        boolean found = false;
        try {
            int cnt = 0;
            while ((entry = zin.getNextEntry()) != null) {
                Kodtar d = resolveFileNameToKodtar(entry.getName());
                List<KodtarElem> deList = importElemekFromFile(entry.getName(), zin, charset);
                if(d != null){
                    d.addAllElem(deList);
                    cnt++;
                }
                list.addAll(deList);
                eredmeny.getImportaltKodtark().add(d);
                found = true;
            }
            //ha annyi zipEntry volt ahány kodtar létrejött akkor exportból csináltuk a kodtarakat!
            eredmeny.setFromExportFile(cnt == eredmeny.getImportaltKodtark().size());
            if (found) {
                eredmeny.setCelpont(list);
                eredmeny.setStatusz(MuveletStatusz.SIKERES);
            } else {
                eredmeny.setCelpont(Collections.EMPTY_LIST);
                eredmeny.addUzenet(UzenetTipus.NEM_IMPORTALHATO, "error.import.format", FileTipus.ZIP.getExtension());
            }
        } catch (Exception ex) {

            eredmeny.setCelpont(Collections.EMPTY_LIST);
            eredmeny.addUzenet(UzenetTipus.NEM_IMPORTALHATO, "error.import.unknown", entry != null ? entry.getName() : "");
            LOGGER.error("Hiba kodtar importalas kozben", ex);
            IOUtils.closeQuietly(zin);
        }
        return eredmeny;
    }

    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    /**
     * A kodtar elemeket tartalmazó fájl feldolgozása.
     * @param fileName 
     * @param zin a fájlra nyitot inputStream
     * @param charset a fájl karakterkódolása
     *
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws RosszKodtarElemAdatFormatumException
     */
    private List<KodtarElem> importElemekFromFile(String fileName, InputStream in, Charset charset)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, 
            NoSuchMethodException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));

        // oszlop fejlécek felolvasása
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new IOException("Ures fajl vagy nem megengedett formatum!");
        }
        String[] headers = firstLine.split(StringUtil.SEMICOLON);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Oszlopok szama: " + headers.length);
        }
        // ha a csv header "név:kodtar uuid formátumú", akkor trükközünk kicsit
        for (int i = 2; i < headers.length; i++) {
            String h = headers[i];
            if (h.indexOf(':') > -1) {
                String attrName = h.substring(0, h.indexOf(':'));
                String kodtarUuid = h.substring(h.indexOf(':') + 1);
                headers[i] = attrName;
                eredmeny.addKodtar(attrName, kodtarUuid);
            }
        }

        // legalább két oszlopnak lennie kell
        if (headers.length < KOTELEZO_OSZLOPOK_SZAMA) {
            LOGGER.error("A kodtar elemeket tartalmazo csv-ben legalabb " + KOTELEZO_OSZLOPOK_SZAMA
                    + " oszlopnak lennie kell: kodtar kod, elem kod, megnevezes");
            eredmeny.addUzenet(UzenetTipus.NEM_IMPORTALHATO, "error.import.kodtar.headerLength", fileName, KOTELEZO_OSZLOPOK_SZAMA);
        }

        // kodtar elemek felolvasása a fájlból
        List<KodtarElem> result = readElemekFromFile(reader, headers);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Beolvasva " + result.size() + " db kodtar elem.");
        }
        eredmeny.addUzenet(UzenetTipus.INFORMACIO,
                "absztraktEredmeny.info.kodtarelemBeolvasva",
                new Object[]{result.size()});

        return result;
    }

    /**
     * Feolvassa a kodtar elemeket a fájlból, és {@link KodtarElem} példányokat
     * hoz létre belőlük.
     *
     * @param reader
     * @param mezok
     * @return
     */
    private List<KodtarElem> readElemekFromFile(BufferedReader reader, String[] mezok)
            throws IOException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {
        List<KodtarElem> result = new ArrayList<KodtarElem>();
        String lineString;
        while ((lineString = reader.readLine()) != null) {
            String[] line = lineString.split(StringUtil.SEMICOLON, mezok.length);
            
            if (line.length >= KOTELEZO_OSZLOPOK_SZAMA) {
                String kodtar = line[0];
                String elemId = line[1];
                String elemMegnevezes = line[2];

                // uj elem es kötelező mezők beállítása
                KodtarElem elem = new KodtarElem(elemId, elemMegnevezes, kodtar);
               
                // egyéb lehetséges mezők beállítása
                for (int i = KOTELEZO_OSZLOPOK_SZAMA; i < mezok.length; i++) {
                    if (line.length <= i) {
                        continue;
                    }
                    if (line[i] != null) {
                        elem.attributum(mezok[i], line[i]);
                    }
                }
                result.add(elem);
            }
        }
        return result;
    }
    /**
     * Az export során keletkezett fájlok nevében a 
     * következő szerkezet van eltárolva: (_ elválasztó karakter)
     * 1. kodtar megnevezés
     * 2. kodtar kód
     * 3. uuid
     * 4. created at time in long ms
     * Ha illeszkedik a fájlneve erre fogjuk beolvasni, ha nem akkor nem!
     * Minden olyan esetben amikor a fájl név nem felel meg null-t 
     * ad vissza egyébként a megfelelő Kodtart
     * @param fileName
     * @return 
     */
    private Kodtar resolveFileNameToKodtar(String fileName){
        final int UUID_LENGTH = 36;//pl. 995a0676-bdcc-4f6d-be97-48e812083824
        
        String[] frags = fileName.split(".csv")[0].split(PATTERN_CHAR);
        if(frags.length != 4  || frags[2].length() != UUID_LENGTH){
            return null;
        }else{
            final String regexp = "^[0-9]+$";
            if(!frags[3].matches(regexp)){
                return null;
            }
            long millis = Long.parseLong(frags[3]);
            Date createdAt = new Date(millis);
            Kodtar d = new Kodtar();                    
            d.setMegnevezes(frags[0]);
            d.setKod(frags[1]);
            d.setId(frags[2]);
            d.setCreatedAt(createdAt);
            return d;
        }
    }
    
    /**
     * Az exportot tamogato metodus, eloallitja
     * a kodtar-bol a filenevet.
     * Az importnal felhasznaljuk ezeket az infokat,
     * amik itt a filenevbe kerulnek.
     * 
     * @param kodtarEntity
     * @return 
     */
    public static String kodtarToFileName(KodtarEntity kodtarEntity){
        StringBuilder fileName = new StringBuilder();
        fileName.append(kodtarEntity.getMegnevezes());
        fileName.append(PATTERN_CHAR);
        fileName.append(kodtarEntity.getKod());
        fileName.append(PATTERN_CHAR);
        fileName.append(kodtarEntity.getId());
        fileName.append(PATTERN_CHAR);
        fileName.append(kodtarEntity.getCreatedAt().getTime());
        fileName.append(".csv");
        
        return fileName.toString();        
    }
}
