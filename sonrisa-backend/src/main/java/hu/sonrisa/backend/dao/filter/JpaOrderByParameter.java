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
package hu.sonrisa.backend.dao.filter;

import java.io.Serializable;

/**
 * @author racka
 */
public class JpaOrderByParameter implements Serializable {
    private String property;
    private boolean isAscending;

    /**
     * 
     * @param property
     * @param ascending
     */
    public JpaOrderByParameter(String property, boolean ascending) {
        this.property = property;
        isAscending = ascending;
    }

    /**
     * 
     * @return
     */
    public String getProperty() {
        return property;
    }

    /**
     * 
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * 
     * @return
     */
    public boolean isAscending() {
        return isAscending;
    }

    /**
     * 
     * @param ascending
     */
    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }
}
