/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.entity;

import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.jaxb.JAXBUtil;
import hu.sonrisa.backend.model.FingerPrinted;
import hu.sonrisa.backend.model.PersistenceAware;
import hu.sonrisa.backend.model.PersistenceAwareBase;
import hu.sonrisa.backend.model.XMLObject;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores a business class wrapped as an XML marshalled string in a blob/clob
 *
 * @param <U>
 * @param <T> egy üzleti osztály, ami XML payload-ként szerializálódik az
 *            adatbázisba
 * @author dobyman
 */
public abstract class XmlWrappedEntity<U extends Serializable, T extends PersistenceAware<U>>
    extends NamedEntity<U> {

    /**
     * logger
     */
    private final static Logger LOGGER = LoggerFactory.
        getLogger(XmlWrappedEntity.class);
    /**
     * Az üzleti osztály
     */
    private Class<T> clazz;

    /**
     *
     * @return
     */
    protected abstract Integer getXmlVersion();

    /**
     *
     * @param xmlVersion
     */
    protected abstract void setXmlVersion(Integer xmlVersion);

    /**
     * Konstruktor
     * <p/>
     * @param clazz az üzleti osztály
     */
    protected XmlWrappedEntity(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * XML payload getter
     * <p/>
     * @return
     */
    protected abstract String getXml();

    /**
     * XML payload setter
     * <p/>
     * @param xml
     */
    protected abstract void setXml(String xml);

    /**
     * Az xml payload-ban tárolt üzleti objektum kivétele
     * <p/>
     * @return
     */
    @Transient
    public final T getWrappedObject() {
        return getWrappedObject(clazz);
    }

    /**
     * Típusos getter a payload-ban tárolt üzleti objektumra
     * <p/>
     * @param clazz
     * @return
     */
    public final T getWrappedObject(Class<T> clazz) {
        T asz;
        try {
            // ha -1, akkor megpróbálja a JAXBUtil felderítnei az XML verzióját
            int xmlVersion = getXmlVersion() == null ? -1 : getXmlVersion();
            if (isXmlNull()) {
                throw new IllegalStateException("" + getId()
                    + BackendExceptionConstants.BEND_00005);
            }
            String payload = getXml();
            if (isGzipped()) {
                payload = JAXBUtil.gunZip(payload);
            }
            StringReader sr = new StringReader(payload);
            asz = JAXBUtil.unmarshalTyped(sr, clazz, xmlVersion);
            sr.close();
            // becsomagolt SAXParseException jöhet itt (!)
        } catch (Exception ex) {
            LOGGER.error("Hiba: " + getXml());
            throw new RuntimeException(ex);
        }
        onUnWrap(asz);
        return asz;
    }

    /**
     * Életciklus metódus - felüldefiniálandó a leszármazott osztályokban.
     *
     * A getWrappedObject hívja meg, hogy a leszármazottak itt definiálhassák
     * azokat a műveleteket, amiket az üzleti objektumon meg akarnak hívni
     * mielőtt a szervíz réteg az üzleti objektumot átadná a többi rétegnek.
     *
     * @param asz
     */
    public void onUnWrap(T asz) {
        asz.setPersistenceID(getId());
        if (asz instanceof FingerPrinted) {
            FingerPrinted f = (FingerPrinted) asz;
            FingerPrinted fThis = (FingerPrinted) this;
            f.setCreatedAt(fThis.getCreatedAt());
            f.setCreatedBy(fThis.getCreatedBy());
            f.setModifiedAt(fThis.getModifiedAt());
            f.setModifiedBy(fThis.getModifiedBy());
        }
        if (asz instanceof PersistenceAwareBase) {
            PersistenceAwareBase p = (PersistenceAwareBase) asz;
            p.setJPAVersion(this.getVersion());
        }
    }

    /**
     * Életciklus metódus - felüldefiniálandó a leszármazott osztályokban.
     *
     * A setWrappedObject hívja meg, hogy az üzleti objektumról át lehessen
     * valahol másolni mezőket, és egyéb logikákat le lehessen futtatni miután
     * egy másik rétegből visszaérkezett az üzleti objektum a perzisztálási
     * rétegbe.
     *
     * @param object
     */
    public void onWrap(T object) {
        if (object instanceof FingerPrinted) {
            FingerPrinted f = (FingerPrinted) this;
            FingerPrinted fob = (FingerPrinted) object;
            f.setModifiedBy(fob.getModifiedBy());
            f.setCreatedBy(fob.getCreatedBy());
            f.setModifiedAt(fob.getModifiedAt());
            f.setCreatedAt(fob.getCreatedAt());
        }
    }

    /**
     * Az üzleti objektum aktuális állapotának visszaírása az XML payload
     * mezőbe. Tipikusan mentés előtt hívódik meg.
     *
     * Ha egyedi logikát tennél az állapotfrissítésbe, használd az onWrap()
     * metódust.
     *
     * @param object
     */
    public final void setWrappedObject(T object) {
        String payload = JAXBUtil.marshal(object);
        setXmlVersion(object instanceof XMLObject ? ((XMLObject) object).
            getObjectVersion() : -1);
        if (isGzipped()) {
            try {
                payload = JAXBUtil.gZip(payload);
            } catch (IOException ex) {
                LOGGER.error("Error gzipping string: " + payload, ex);
            }
        }
        setXml(payload);
        onWrap(object);
    }

    protected boolean isXmlNull() {
        return getXml() == null;
    }

    protected boolean isGzipped() {
        return false;
    }
}