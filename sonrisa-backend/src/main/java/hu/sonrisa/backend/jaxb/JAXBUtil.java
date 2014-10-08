/*
 *  *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Sonrisa Informatikai Kft. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sonrisa.
 *
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.jaxb;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import hu.sonrisa.backend.exception.BackendException;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.model.PersistenceAwareBase;
import hu.sonrisa.backend.model.XMLObject;
import hu.sonrisa.backend.model.util.InputStreamUtil;
import org.apache.xmlbeans.impl.util.Base64;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * JAXB funkciók
 * 
 * @author cserepj
 */
public final class JAXBUtil {

    /**
     * A verzióváltó descriptor cache kezdeti mérete
     */
    private static final int CACHE_INITIAL_SIZE = 10;
    private static final String UTF8 = "UTF-8";
    /**
     * Woodstox XML InputFactory
     */
    private static XMLInputFactory2 xmlif;
    /**
     * Woodstox XML OutputFactory
     */
    private static XMLOutputFactory2 xmlof;
    /**
     * JAXBContext cache osztályonként eltárolva
     * A JAXBContext gyártása lassú és drága művelet, ezért ezeket eltesszük későbbi használatra.
     */
    private static Map<Class, JAXBContext> cache = new ConcurrentHashMap<Class, JAXBContext>(CACHE_INITIAL_SIZE);
    private static Map<Class, XmlVerzioValtasDescriptor> verziok = new ConcurrentHashMap<Class, XmlVerzioValtasDescriptor>(CACHE_INITIAL_SIZE);
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBUtil.class);
    private static final String OBJECT_VERSION_STRING = "objectVersion=\"";
    /**
     * Szemafor ami biztosítja, hogy maximum a processzorok számával meghatározott
     * unmarshal fusson egyszerre az alkalmazásban
     */
    private static final Semaphore SEMAPHORE = new Semaphore(
            2 * Runtime.getRuntime().availableProcessors());

    /**
     * A factory-k inicializálása
     */
    static {
        createFactory();
    }

    private JAXBUtil() {
    }

    /**
     * Az átadott objektum XML reprezentációba konvertálása
     * @param object
     * @return
     */
    public static String marshal(Object object) {
        long begin = System.currentTimeMillis();
        try {
            return marshal(object, object.getClass(), false);
        } finally {
            long end = System.currentTimeMillis();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Marshall took: " + (end - begin) + "ms: " + object);
            }
        }
    }

    /**
     * Az átadott objektum XML reprezentációba konvertálása
     * @param object
     * @param prettyPrint ha true, akkor az XML tagolva, nestelve és több sorban íródik ki
     * @return
     */
    public static String marshal(Object object, boolean prettyPrint) {
        long begin = System.currentTimeMillis();
        try {
            return marshal(object, object.getClass(), prettyPrint);
        } finally {
            long end = System.currentTimeMillis();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Marshall took: " + (end - begin) + "ms: " + object);
            }
        }
    }

    /**
     * GZIP tömörítésen futtatja át az átadott String-et, majd Base64 encode-olja
     * 
     * @param base64
     * @return
     * @throws IOException 
     */
    public static String gZip(String base64) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(bos);
        gos.write(base64.getBytes(UTF8));
        gos.flush();
        gos.close();
        return new String(Base64.encode(bos.toByteArray()), UTF8);
    }

    /**
     * GZIP kitömörítés Base64 dekódolás után
     * 
     * @param base64string
     * @return
     * @throws IOException 
     */
    public static String gunZip(String base64string) throws IOException {
        byte[] bytes = Base64.decode(base64string.getBytes(UTF8));
        GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(bytes));
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(gin, UTF8));
        String l = null;
        while ((l = br.readLine()) != null) {
            sb.append(l);
        }
        return sb.toString();
    }

    /**
     * Az átadott objektum XML reprezentációba konvertálása
     * @param object
     * @param clazz
     * @param prettyPrint
     * @param w Output writer
     */
    private static void marshal(Object object, Class clazz, boolean prettyPrint, Writer w) {
        XMLStreamWriter2 writer = null;
        try {
            // szemaforra várunk
            SEMAPHORE.acquire();
            Marshaller m = getMarshaller(clazz);
            if (prettyPrint) {
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
                m.marshal(object, w);
            } else {
                writer = (XMLStreamWriter2) xmlof.createXMLStreamWriter(w);
                m.marshal(object, writer);
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        } catch (JAXBException ex) {
            throw new IllegalStateException(ex);
        } catch (XMLStreamException ex) {
            throw new IllegalStateException(ex);
        } finally {
            // szemafort elengedjük
            SEMAPHORE.release();
            if (writer != null) {
                try {
                    w.close();
                    writer.close();
                } catch (Exception ioe) {
                    throw new IllegalStateException(BackendExceptionConstants.BEND_00016, ioe);
                }
            }
        }
    }

    /**
     * Az átadott objektum XML reprezentációba konvertálása
     * @param object
     * @param clazz
     * @param prettyPrint
     * @return
     */
    private static String marshal(Object object, Class clazz, boolean prettyPrint) {
        StringWriter w = new StringWriter();
        marshal(object, clazz, prettyPrint, w);
        return w.toString();
    }

    /**w
     * Az átadott reader-ből kiolvasható objektumot adja vissza.
     * 
     * @param reader
     * @param classes
     * @return
     */
    private static Object unmarshal(Reader reader, Class classes) {
        try {
            SEMAPHORE.acquire();
            Object o = null;
            try {
                Unmarshaller m = getUnmarshaller(classes);
                XMLStreamReader xsr = xmlif.createXMLStreamReader(reader);
                o = m.unmarshal(xsr);
                xsr.close();
                return o;
            } catch (JAXBException ex) {
                throw new IllegalStateException(ex);
            } catch (XMLStreamException ex) {
                throw new IllegalStateException(ex);
            } finally {
                SEMAPHORE.release();
            }
        } catch (InterruptedException ex) {
            LOGGER.error("Thread was interrupted", ex);
            throw new BackendException(ex);
        }
    }

    private static XmlVerzioValtasDescriptor getVerzioValtasDescriptor(Class clazz) {
        XmlVerzioValtasDescriptor x = verziok.get(clazz);
        if (x == null) {
            x = new XmlVerzioValtasDescriptor(clazz);
            verziok.put(clazz, x);
        }
        return x;
    }

    /**
     * Típusos unmarshall az átadott reader-ből. Megadható az XML-ben várt
     * objektum osztálya és objektum verziója. Ha -1-et adunk át verzióként
     * azzal automatikus verziókeresést érünk el ami a stream első 1000
     * karakterében fogja keresni az esetleges verziót jelző stringet.
     * 
     * @param <T>
     * @param reader Reader
     * @param clazz
     * @param version
     * @return
     */
    public static <T> T unmarshalTyped(Reader reader, Class<T> clazz, int version) {
        T obj;
        long begin = System.currentTimeMillis();

        if (XMLObject.class.isAssignableFrom(clazz)) {
            CharSequence c = InputStreamUtil.getString(reader);
            if (version == -1) {
                version = determineVersion(c);
            }
            XmlVerzioValtasDescriptor vvd =
                    getVerzioValtasDescriptor(clazz);
            if (vvd.isTransformable(version)) {
                c = vvd.transform(c, version);
            }
            StringReader sr = new StringReader(c.toString());
            obj = (T) unmarshal(sr, clazz);
            sr.close();
        } else {
            obj = (T) unmarshal(reader, clazz);
        }
        long end = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unmarshall took: " + (end - begin) + "ms: " + obj);
        }
        return obj;
    }

    /**
     * Típusos unmarshall az inpustreamből. Megadható az XML-ben lévő objektum
     * osztálya. Az objektumverzió megállapítása automatikus lesz.
     *
     * @param <T>
     * @param inputStream
     * @param clazz
     * @return
     */
    public static <T> T unmarshalTyped(InputStream inputStream, Class<T> clazz) {
        try {
            return unmarshalTyped(new InputStreamReader(inputStream, UTF8), clazz, -1);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Típusos unmarshall az átadott streamből. Megadható az XML-ben várt
     * objektum osztálya és objektum verziója. Ha -1-et adunk át verzióként
     * azzal automatikus verziókeresést érünk el ami a stream első 1000
     * karakterében fogja keresni az esetleges verziót jelző stringet.
     *
     * @param <T>
     * @param is
     * @param clazz
     * @param version
     * @return
     */
    public static <T> T unmarshalTyped(InputStream is, Class<T> clazz, int version) {
        try {
            return unmarshalTyped(new InputStreamReader(is, UTF8), clazz, version);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Típusos unmarshall az inpustreamből. Megadható az XML-ben lévő objektum
     * osztálya. Az objektumverzió megállapítása automatikus lesz.
     *
     * @param <T>
     * @param inputStream
     * @param clazz
     * @return
     */
    public static <T> T unmarshalTyped(Reader reader, Class<T> clazz) {
        return unmarshalTyped(reader, clazz, -1);
    }

    /**
     * Unmarshaller gyártása az átadott osztályhoz
     * @param clazz
     * @return
     * @throws JAXBException
     */
    private static Unmarshaller getUnmarshaller(Class clazz) throws JAXBException {
        JAXBContext c = cache.get(clazz);
        if (c == null) {
            c = JAXBContext.newInstance(clazz);
            cache.put(clazz, c);
        }
        return c.createUnmarshaller();
    }

    /**
     * Marshaller gyártása az átadott osztályhoz
     * @param classes
     * @return
     * @throws JAXBException
     */
    private static Marshaller getMarshaller(Class classes) throws JAXBException {
        JAXBContext c = cache.get(classes);
        if (c == null) {
            c = JAXBContext.newInstance(classes);
            cache.put(classes, c);
        }
        return c.createMarshaller();
    }

    /**
     * Factory-k létrehozása
     */
    private static void createFactory() {

        try {
            xmlif = new WstxInputFactory();
            xmlif.setProperty(
                    XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                    Boolean.FALSE);
            xmlif.setProperty(
                    XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                    Boolean.FALSE);
            xmlif.setProperty(
                    XMLInputFactory.IS_COALESCING,
                    Boolean.FALSE);
            xmlif.configureForSpeed();
            xmlof = new WstxOutputFactory();
            xmlof.configureForSpeed();
        } catch (Exception ex) {
            LOGGER.error("Exception creating factory", ex);
        }
    }

    /**
     * Az átadott XML reprezentációban megkeresi az objektum 
     * verzióját
     * 
     * @param c
     * @return 
     */
    public static int determineVersion(CharSequence c) {
        int version = 0;
        // első 2000 karaktert vizsgáljuk
        String prefix = (c.length() > 2000 ? c.subSequence(0, 2000) : c).toString();
        int idx = prefix.indexOf(OBJECT_VERSION_STRING);
        if (idx > -1) {
            idx += OBJECT_VERSION_STRING.length();
            while (true) {
                int ch = c.charAt(idx);
                if (ch == '"') {
                    return version;
                }
                if (ch > '9' || ch < '0') {
                    throw new IllegalArgumentException(prefix);
                }
                version = version * 10;
                version += (ch - '0');
                idx++;
            }
        }
        return version;
    }

    /**
     * JAXB mappelt osztály klónozása marshall/unmarshall hívásával
     * 
     * @param <T>
     * @param object
     * @return 
     */
    public static <T> T clone(T object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("JAXB Cloning object: " + object);
        }
        String s = marshal(object, object.getClass(), false);
        T newObject = (T) unmarshal(new StringReader(s), object.getClass());
        if (object instanceof PersistenceAwareBase) {
            PersistenceAwareBase p = ((PersistenceAwareBase) newObject);
            PersistenceAwareBase oldPersisted = (PersistenceAwareBase) object;
            p.setPersistenceID(oldPersisted.getPersistenceID());
            p.setJPAVersion(oldPersisted.getJPAVersion());
        }
        return newObject;
    }
}
