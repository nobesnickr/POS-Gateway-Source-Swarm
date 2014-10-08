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
package hu.sonrisa.backend.model.util;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Utility methods for dates
 *
 * @author sonrisa
 */
public class DateUtilTest {

    /**
     * Teszteset: valid zárt intervallum Elvárt működés: true-t ad vissza
     */
    @Test
    public void testZartIntervallum() {
        Calendar start = GregorianCalendar.getInstance();
        start.set(101 + 1900, 2, 1);

        Calendar end = GregorianCalendar.getInstance();
        start.set(101 + 1900, 10, 1);

        boolean result = DateUtil.validateInterval(start.getTime(), end.getTime(), false);
        assertTrue(result);
    }

    /**
     * Teszteset: egyik végén nyitott intervallum Elvárt működés: true-t ad
     * vissza
     */
    @Test
    public void testNyitottIntervallum() {
        Calendar date = GregorianCalendar.getInstance();
        date.set(101 + 1900, 2, 1);

        // felül nyitott
        boolean result = DateUtil.validateInterval(date.getTime(), null, false);
        assertTrue(result);

        // alul nyitott
        result = DateUtil.validateInterval(null, date.getTime(), false);
        assertTrue(result);
    }

    /**
     * Teszteset: mindkét végén nyitott intervallum Elvárt működés: true-t ad
     * vissza
     */
    @Test
    public void testNyitottIntervallum2() {
        // felül nyitott
        boolean result = DateUtil.validateInterval(null, null, false);
        assertTrue(result);
    }

    /**
     * Teszteset: rossz intervallum Elvárt működés: false-t ad vissza
     */
    @Test
    public void testHibasIntervallum() {
        Calendar start = GregorianCalendar.getInstance();
        start.set(101 + 1900, 2, 1);

        Calendar end = GregorianCalendar.getInstance();
        start.set(101 + 1900, 10, 1);

        boolean result = DateUtil.validateInterval(end.getTime(), start.getTime(), false);
        assertFalse(result);
    }

    /**
     * Teszteset: nyitó és záró dátum megegyezik Elvárt működés: equalsIsValid
     * értékétől függ a visszatérési érték.
     */
    @Test
    public void testNullaHosszuIntervallum() {
        Calendar date = GregorianCalendar.getInstance();
        date.set(101 + 1900, 2, 1);
        boolean result = DateUtil.validateInterval(date.getTime(), date.getTime(), false);
        assertFalse(result);
        result = DateUtil.validateInterval(date.getTime(), date.getTime(), true);
        assertTrue(result);
    }

    @Test
    public void testValid() {
        assertTrue(DateUtil.validInterval(null, null)); // minusz végtelen - plusz végtelen
        assertTrue(DateUtil.validInterval(new Date(), null)); // mától világvégéig
        assertTrue(DateUtil.validInterval(null, new Date())); // big bangtől máig
        Date d = new Date();
        assertFalse(DateUtil.validInterval(d, d)); // üres intervallum nem valid
    }
}
