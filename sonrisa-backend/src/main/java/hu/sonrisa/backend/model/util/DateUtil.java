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

import hu.sonrisa.backend.model.Interval;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author Golyo
 */
public final class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String PRINT_DATE_FORMAT = "yyyy.MM.dd HH:mm";
    public static final String FULL_DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    protected static final int[] FIELDS = {Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};

    public static Interval getInterval(Interval v) {
        return createInterval(v.getErvenyessegKezdete(), v.getErvenyessegVege());
    }

    public static Interval extendInterval(Interval v, Interval w) {
        Date s1 = DateUtil.minStartDate(v.getErvenyessegKezdete(), w.getErvenyessegKezdete());
        Date s2 = DateUtil.maxEndDate(v.getErvenyessegVege(), w.getErvenyessegVege());
        return createInterval(s1, s2);
    }

    public static Date addDays(Date date, int i) {
        if (date == null) {
            return null;
        }
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, i);
        return c.getTime();
    }

    private DateUtil() {
    }

    public static String format(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static boolean datesEqual(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }

    public static DateFormat getDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * Kerkíti a dátumot 23 óra 59 perc, 59 sec, 99 ms-re.
     *
     * @param date
     * @return
     */
    public static Date getMaxTimeDate(Date date) {
        return getMaxTimeDate(date, 0);
    }

    /**
     *
     * @param date
     * @param addDate
     * @return
     */
    public static Date getMaxTimeDate(Date date, int addDate) {
        return getConvertedDate(date, addDate, true);
    }

    /**
     * Kerkíti a dátumot 0 óra 0 perc, 0 sec, 0 ms-re.
     *
     * @param date
     * @return
     */
    public static Date getMinTimeDate(Date date) {
        return getMinTimeDate(date, 0);
    }

    /**
     *
     * @param date
     * @param addDate
     * @return
     */
    public static Date getMinTimeDate(Date date, int addDate) {
        return getConvertedDate(date, addDate, false);
    }

    /**
     * Ellenőrzi, hogy a két dátum valóban egy érvényes intervalumot alkot-e,
     * azaz a
     * <code>start</code> valóban korábban van-e mint az
     * <code>end</code>.
     * <p/>
     *
     * Nyitott intervallumokat is kezel, azaz ha valamelyik, vagy mindkét dátum
     * null, azt is elfogadja.
     * <p/>
     *
     * Ha az
     * <code>equalsIsValid</code> false, akkor a két dátum egyezése esetén is
     * false-t ad vissza. Különben egyezőség esetén true a visszatérési érték.
     *
     *
     * @param start intervallum kezdő dátuma
     * @param end intervallum záró dátuma
     *
     * @return true, ha a két dátum egy intervallumot alkot, <br/> különben
     * false
     */
    public static boolean validateInterval(final Date start, final Date end, boolean equalsIsValid) {
        boolean result = true;
        if (start != null && end != null) {
            result = start.before(end);

            // ha az egyenlőséget is elfogadjuk, akkor azt is ellenőrzi
            if (equalsIsValid) {
                result = result || start.equals(end);
            }
        }
        return result;
    }

    /**
     * Returns a date which is exactly the given date plus the given days and
     * either the end of it or start of it
     *
     * @param date - we should add to
     * @param addDate - how many calendar days
     */
    private static Date getConvertedDate(Date date, int addDate, boolean isMax) {
        if (date != null) {
            Calendar gc = GregorianCalendar.getInstance();
            gc.setTime(date);
            gc.add(Calendar.DATE, addDate);
            for (int i : FIELDS) {
                int value = isMax ? gc.getMaximum(i) : gc.getMinimum(i);
                gc.set(i, value);
            }
            return gc.getTime();
        } else {
            return null;
        }
    }

    public static boolean validInterval(Date start, Date end) {
        long s1 = start == null ? Long.MIN_VALUE : start.getTime();
        long e1 = end == null ? Long.MAX_VALUE : end.getTime();
        return s1 < e1;
    }

    /**
     * null = plusz végtelen
     *
     * @param end1
     * @param end2
     * @return
     */
    public static Date minEndDate(Date end1, Date end2) {
        long e1 = end1 == null ? Long.MAX_VALUE : end1.getTime();
        long e2 = end2 == null ? Long.MAX_VALUE : end2.getTime();
        return e1 <= e2 ? end1 : end2;
    }

    /**
     * null = plusz végtelen
     *
     * @param end1
     * @param end2
     * @return
     */
    public static Date maxEndDate(Date end1, Date end2) {
        long e1 = end1 == null ? Long.MAX_VALUE : end1.getTime();
        long e2 = end2 == null ? Long.MAX_VALUE : end2.getTime();
        return e1 >= e2 ? end1 : end2;
    }

    /**
     * null = plusz végtelen
     *
     * @param end1
     * @param end2
     * @return
     */
    public static Date minStartDate(Date end1, Date end2) {
        long e1 = end1 == null ? Long.MIN_VALUE : end1.getTime();
        long e2 = end2 == null ? Long.MIN_VALUE : end2.getTime();
        return e1 <= e2 ? end1 : end2;
    }

    /**
     * null = plusz végtelen
     *
     * @param end1
     * @param end2
     * @return
     */
    public static Date maxStartDate(Date end1, Date end2) {
        long e1 = end1 == null ? Long.MIN_VALUE : end1.getTime();
        long e2 = end2 == null ? Long.MIN_VALUE : end2.getTime();
        return e1 >= e2 ? end1 : end2;
    }

    public static boolean overlap(Date start1, Date end1, Date start2, Date end2) {
        long s1 = start1 == null ? Long.MIN_VALUE : start1.getTime();
        long e1 = end1 == null ? Long.MAX_VALUE : end1.getTime();
        long s2 = start2 == null ? Long.MIN_VALUE : start2.getTime();
        long e2 = end2 == null ? Long.MAX_VALUE : end2.getTime();
        if (s1 <= e2 && (s2 <= e1)) {
            return true;
        }
        return false;
    }

    public static boolean overlap(Interval i1, Interval i2) {
        return overlap(i1.getErvenyessegKezdete(), i1.getErvenyessegVege(), i2.getErvenyessegKezdete(), i2.getErvenyessegVege());
    }

    public static Interval createInterval(final Date i1, final Date i2) {
        if (i1 == null || i2 == null || i1.before(i2)) {
            return new Interval() {
                public Date getErvenyessegKezdete() {
                    return i1;
                }

                public Date getErvenyessegVege() {
                    return i2;
                }
            };
        }
        return new Interval() {
            public Date getErvenyessegKezdete() {
                return i2;
            }

            public Date getErvenyessegVege() {
                return i1;
            }
        };
    }

    public static boolean between(Interval interval, Date date, boolean nullDateMeansPast) {
        long s1 = interval.getErvenyessegKezdete() == null ? Long.MIN_VALUE : interval.getErvenyessegKezdete().getTime();
        long e1 = interval.getErvenyessegVege() == null ? Long.MAX_VALUE : interval.getErvenyessegVege().getTime();
        long d = date == null ? (nullDateMeansPast ? Long.MIN_VALUE : Long.MAX_VALUE) : date.getTime();
        return s1 <= d && d <= e1;
    }
}
