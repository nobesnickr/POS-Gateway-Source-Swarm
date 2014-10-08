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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Joe
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }
    /**
     * Objektum listát rendez az egyik member alapján.
     * 
     * @param list - NPE-t dob ha null a lista, üres listánál nem csinál semmit!
     * @param propertyName
     * @param isAscending 
     */
    public static void sortByProperty(final List<? extends Object> list, final String propertyName, final boolean isAscending){
        
        Collections.sort(list, new Comparator<Object>() {
            
                    @Override
                    public int compare(Object h1, Object h2) {
                        Comparable prop1 = (Comparable) ReflectionUtil.get(h1, propertyName);
                        Comparable prop2 = (Comparable) ReflectionUtil.get(h2, propertyName);
                        
                        if (prop1 != null) {
                            int ret = prop2 != null ? prop1.compareTo(prop2) : 1;
                            return isAscending ? ret : -ret;
                        } else {
                            return prop2 != null ? (isAscending ? -1 : 1) : 0;
                        }
                    }
                });
    }
    
}
