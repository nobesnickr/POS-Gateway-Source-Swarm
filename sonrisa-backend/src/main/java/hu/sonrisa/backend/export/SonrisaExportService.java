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
package hu.sonrisa.backend.export;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.model.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.commons.io.IOUtils;

/**
 * Export service
 *
 * @author Golyo
 */
public abstract class SonrisaExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonrisaExportService.class);
    
    private static final String TIMES_TTF = "times.ttf";
    private static final String EXP_RES_PROPS = "exportResources.properties";
    private Properties resources;
    private final BaseFont baseFont;
    private static final String DELIMITER = ", ";

    /**
     * Konstruktor betölti az exportResources.properties fájlt a classpath-ról, 
     * a leszármazott osztály resource-ai között keresi a properties fájlt!
     * Betölti még a times.ttf fájlt is
     */
    public SonrisaExportService() {
        resources = new Properties();
        InputStream resourceIns = null;
        try {
            Class cl = this.getClass();
            while (resourceIns == null && cl != null) {
                resourceIns = (cl.getResourceAsStream(EXP_RES_PROPS));
                cl = cl.getSuperclass();
            }
            if (resourceIns == null) {
                throw new IllegalStateException(BackendExceptionConstants.BEND_00014 + this.getClass().getName());
            }
            resources.load(resourceIns);
            byte[] font = loadResource(TIMES_TTF);
            baseFont = BaseFont.createFont(TIMES_TTF, BaseFont.CP1250, BaseFont.EMBEDDED, true, font, null, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException de) {
            throw new RuntimeException(de);
        } finally {
            if (resourceIns != null) {
                IOUtils.closeQuietly(resourceIns);
            }
        }
    }

    private byte[] loadResource(String name) {
        final int buffSize = 1024;
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = SonrisaExportService.class.getResourceAsStream(name);
            if (is == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(name + " resource not found for class " + 
                            SonrisaExportService.class.getName() + " trying " + this.getClass().getName());
                }
                is = this.getClass().getResourceAsStream(name);
            }
            if (is == null) {
                LOGGER.debug("No resource found: " + name);
                throw new RuntimeException("No resource found for: " + name);
            }
            bos = new ByteArrayOutputStream();
            byte[] vals = new byte[buffSize];
            int c;
            while ((c = is.read(vals)) >= 0) {
                bos.write(vals, 0, c);
            }
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if(is != null){
                    is.close();
                }
                if(bos != null){    
                    bos.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Rendbeteszi kicsit a fájl nevet:
     * <ul>
     * <li>üres string-re cseréli a slash(/) karaktereket</li>
     * <li>üres string-re cseréli a backslash(\) karaktereket</li>
     * <li>ékezet nélküli megfelelőjükre cseréli az ékezetes karaktereket</li>
     * <ul>
     *
     * @param str
     * @return
     */
    protected static String normalizeFileName(final String str) {
        String fajlNevStr = StringUtil.removeSlash(str);
        fajlNevStr = StringUtil.removeBackslash(fajlNevStr);
        return StringUtil.removeAccents(fajlNevStr);
    }

    protected <T extends Enum> String enumToString(T value) {
        if (value != null) {
            return resources.getProperty(value.getDeclaringClass().
                    getSimpleName() + "." + value.name(), value.name());
        } else {
            return "";
        }
    }

    protected <T extends Enum> String enumIterableToString(Iterable<T> iterable) {
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = iterable.iterator();
        if (it.hasNext()) {
            sb.append(enumToString(it.next()));
            while (it.hasNext()) {
                sb.append(DELIMITER);
                sb.append(enumToString(it.next()));
            }
        }
        return sb.toString();
    }

    protected static <T> String objectIterableToString(Iterable<T> iterable) {
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = iterable.iterator();
        if (it.hasNext()) {
            sb.append(it.next().toString());
            while (it.hasNext()) {
                sb.append(DELIMITER);
                sb.append(it.next().toString());
            }
        }
        return sb.toString();
    }

    public BaseFont getBaseFont() {
        return baseFont;
    }

    public Properties getResources() {
        return resources;
    }
}
