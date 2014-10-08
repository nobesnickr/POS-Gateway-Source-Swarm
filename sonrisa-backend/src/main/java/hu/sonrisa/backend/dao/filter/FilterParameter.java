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

import javax.persistence.Query;

/**
 * Egy where clause-béli elemet reprezentáló belső osztály. Kétféleképpen használható:
 * <ul>
 * <li>Egy QueryFilter konstruktorában
 * <li>Egy SimpleFilter-ben.
 * </ul>
 * A kétféle felhasználás némileg különbözik: a SimpleFilter-ben a property az entitás
 * valamelyik attribútumát jelöli, a QueryFilter-ben viszont a query string egy paraméterét
 * jelöli. Az object mindkét esetben a behelyettesítendő objektumot takarja. Az operator
 * csak a SimpleFilter-ben hasznos, a QueryFilter figyelmen kívül hagyja.
 *
 * @author cserepj
 */
public class FilterParameter {

    private String property;
    private Object object;
    private String operator;

    /**
     * 
     */
    public FilterParameter() {
    }

    /**
     * 
     * @param property
     * @param object
     * @param operator
     */
    public FilterParameter(String property, Object object, String operator) {
        this.property = property;
        this.object = object;
        this.operator = operator;
    }

    /**
     * 
     * @param property
     * @param object
     */
    public FilterParameter(String property, Object object) {
        this.property = property;
        this.object = object;
        this.operator = " = ";
    }

    /**
     * 
     * @param query
     * @param cnt
     */
    public void append(StringBuilder query, int cnt) {
        query.append("t.").append(property);
        if (object != null) {
            query.append(operator).
                    append(" :").append("param").append(cnt);
        } else {
            query.append(" is null ");
        }
    }

    /**
     * 
     * @param query
     * @param cnt
     */
    public void setParameter(Query query, int cnt) {
        if (object != null) {
            query.setParameter("param" + cnt, object);
        }
    }

    @Override
    public String toString() {
        return "FilterParameter{" + "property=" + property + ", object=" + object + ", operator=" + operator + '}';
    }

    Object getObject() {
        return object;
    }

    String getProperty() {
        return property;
    }

    String getOperator() {
        return operator;
    }
}
