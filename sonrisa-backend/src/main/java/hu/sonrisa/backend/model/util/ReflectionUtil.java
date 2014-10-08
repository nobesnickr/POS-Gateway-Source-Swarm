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
package hu.sonrisa.backend.model.util;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Osztályokon reflexióval végzett műveletek.
 *
 */
public final class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
    }
    /**
     * Property értékét lehet lekérdezni ezzel a metódussal, név alapján.
     * Ha nincs ilyen nevű property null-t ad vissza.
     * <p/>
     *
     * <strong>Figyelem:</strong> boolean típusú property-re nem működik! (azaz isXxxxxx() jellegű getterekre)
     *
     * @param obj
     * @param propName
     * @return
     */
    public static Object get(Object obj, String propName) {
        if (obj != null) {
            try {
                Method method = obj.getClass().getMethod("get" + propName.substring(0, 1).toUpperCase() + propName.substring(1));
                return method.invoke(obj);
            } catch (Exception ex) {
                // fail-safe, nem szallhat el, legfeljebb null-t ad vissza
                LOGGER.warn("Nem sikerült a get művelet: " + obj.toString() + " prop: " + propName);
                LOGGER.warn("Hiba oka: ",ex);
            }
        }
        return null;
    }

    /**
     * 
     * @param obj
     * @param propName
     * @param propertyValue
     * @param propertyType
     * @return
     */
    public static Object set(Object obj, String propName, Object propertyValue, Class propertyType) {
        if (obj != null) {
            try {
                Method method = obj.getClass().getMethod("set" + propName.substring(0, 1).toUpperCase() +
                        propName.substring(1), propertyType);
                return method.invoke(obj, propertyValue);
            } catch (Exception ex) {
                // fail-safe, nem szallhat el, legfeljebb null-t ad vissza
                LOGGER.warn("Nem sikerült a set művelet: " + obj.toString() + " prop: " + propName + " value: " + propertyValue);
                LOGGER.warn("Hiba oka: ",ex);
            }
        }
        return null;
    }  
}
