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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Golyo
 */
public final class LocaleUtil {
    public static final char DECIMAL_SEPARATOR = ',';
    // Space is not valid thousands-separator, but no-br space is.
    public static final char GROUPPING_SEPARATOR = '\u00A0';
    
    //Csak akkor látszik a tizedesjegy ha van
    public static final DecimalFormat DISPLAY_FORMAT = new DecimalFormat("##0.####");
    //Mindenképp látszik tizedesjegy
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.0###");
    //Nem látszik tizedesjegy
    public static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("##0");
    
    public static Map<String, Locale> LOCALE_MAP = Collections.synchronizedMap(new HashMap<String, Locale>());
    
    public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS;
    static {
        DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(DECIMAL_SEPARATOR);
        DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator(GROUPPING_SEPARATOR);
        
        DECIMAL_FORMAT.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);
        DECIMAL_FORMAT.setParseBigDecimal(true);
        
        DISPLAY_FORMAT.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);
        DISPLAY_FORMAT.setParseBigDecimal(true);
    }
    
    private LocaleUtil() { 
    }
        
    public static Locale getLocale(String language) {
        Locale loc = LOCALE_MAP.get(language);
        if (loc == null) {
            loc = findLocale(language);
            LOCALE_MAP.put(language, loc);
        }
        return loc;
    }
    
    private static Locale findLocale(String language) {
        for (Locale l: Locale.getAvailableLocales()) {
            if (language.equals(l.getLanguage())) {
                return l;
            }
        }
        return new Locale(language);
    }
}
