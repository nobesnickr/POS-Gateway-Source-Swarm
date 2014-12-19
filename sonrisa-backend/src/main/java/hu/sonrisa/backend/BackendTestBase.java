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
package hu.sonrisa.backend;

import hu.sonrisa.backend.async.HatterFolyamat;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.model.ResourceBasedUzenet;
import hu.sonrisa.backend.model.util.ReflectionUtil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public class BackendTestBase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendTestBase.class);
    /**
     * Visszaad egy Date objektumot a kapott dátumra beállítva.
     *      
     * @param year
     * @param month
     * @param date
     * @return 
     */
    protected Date getDatum(int year, int month, int date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(year, month, date);
        return cal.getTime();
    }

    /**
     * Lefuttat egy háttér folyamatot, majd ellenőrzi,
     * hogy volt-e kivétel a futás közben.
     * 
     * @param folyamat 
     */
    protected void folyamatRunAndAssert(HatterFolyamat folyamat) {
        if (folyamat == null) {
            throw new IllegalArgumentException(BackendExceptionConstants.BEND_00001);
        }
        folyamat.run();
        LOGGER.info(Arrays.toString(folyamat.getUzenetek().toArray()));
        asssertNotContainsException(folyamat.getUzenetek());
        folyamat.isMegszakithato();
    }

    /**
     * 
     * @param uzenetek
     */
    protected void asssertNotContainsException(List<ResourceBasedUzenet> uzenetek) {
        for (ResourceBasedUzenet u : uzenetek) {
            if (u.getResourceKey().contains("exception")) {
                LOGGER.warn(Arrays.toString(u.getParameters()));
                throw new IllegalStateException();
            }
        }
    }
     
    /**
     * Megkeresi az üzenetek között az elsőt, amelyik az átadott kulccsal rendelkezik.
     * Ha nincs ilyen, akkor null-t ad vissza.
     * 
     * @param folyamat
     * @param key
     * @return 
     */
    protected ResourceBasedUzenet findUzenet(HatterFolyamat folyamat, String key) {
        List<ResourceBasedUzenet> uzenetek = findUzenetek(folyamat, key);
        return uzenetek.isEmpty() ? null : uzenetek.get(0);
    }
    
    /**
     * Megkeresi az üzenetek között az elsőt, amelyik az átadott kulccsal, és paraméterekkel (sorrendhelyesen) rendelkezik.
     * Ha nincs ilyen, akkor null-t ad vissza.
     * @param folyamat
     * @param key
     * @param params
     * @return 
     */
    protected ResourceBasedUzenet findUzenet(HatterFolyamat folyamat, String key, Object... params) {
        List<ResourceBasedUzenet> uzenetek = new ArrayList<ResourceBasedUzenet>();
        for (ResourceBasedUzenet u : folyamat.getUzenetek()) {
            if (u.getResourceKey().equals(key) && Arrays.equals(u.getParameters(), params)) {
                uzenetek.add(u);
            }
        }
        return uzenetek.isEmpty() ? null : uzenetek.get(0);
    }
    
    /**
     * Megkeresi az üzenetek között az összeset, amelyek az átadott kulccsal rendelkeznek.
     * Ha nincs egy se, akkor üres listát ad vissza.
     * 
     * @param folyamat
     * @param key
     * @return 
     */
    protected List<ResourceBasedUzenet> findUzenetek(HatterFolyamat folyamat, String key){
        List<ResourceBasedUzenet> result = new ArrayList<ResourceBasedUzenet>();

        for (ResourceBasedUzenet u : folyamat.getUzenetek()) {
            if (u.getResourceKey().equals(key)) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * 
     * @param folyamat
     * @param key
     * @param paramIdx
     * @param o
     */
    protected void assertUzenetParamEquals(HatterFolyamat folyamat, String key, int paramIdx, Object o) {
        ResourceBasedUzenet uzenet = findUzenet(folyamat, key);
        if (uzenet == null) {
            throw new IllegalArgumentException();
        }
        if (uzenet.getParameters().length < paramIdx) {
            throw new IllegalArgumentException(BackendExceptionConstants.BEND_00002 + paramIdx + "/" + uzenet.getParameters().length);
        }
        if (!o.equals(uzenet.getParameters()[paramIdx])) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ellenőrzi, hogy a listában van-e olyan elem, aminek az adott nevű property-je
     * megegyezik a kapott értékkel.
     * 
     * @param list
     * @param propertyName
     * @param value
     * @return 
     */
    protected boolean containsWithProperty(final List<? extends Object> list, final String propertyName, final Object value) {
        for (Object item : list) {
            final Object itemValue = ReflectionUtil.get(item, propertyName);
            if ((value == null && itemValue == null) || (value != null && value.equals(itemValue))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param filename
     * @return
     */
    public static InputStream getInputStream(String filename) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    }
}
