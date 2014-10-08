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

import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Egy adott entitás különböző property-k alapján történő lekérdezésére
 * szolgáló filter. Kifejezetten nem alkalmas join-ok és egyéb összetettebb
 * műveletek kezelésére, ne is próbáljuk ilyesmire használni.
 * 
 * @param <T> A SonrisaJPAEntity leszármazott, amire a lekérdezés szól
 * 
 * @author cserepj
 */
public class SimpleFilter<T extends SonrisaJPAEntity<?>> implements ProviderJpaFilter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFilter.class);
    private static final int DEF_INITIAL_CAPACITY = 50;
    private Class<T> clazz;
    private FilterParameter[] parameters;
    private String sort;

    /**
     * Konstruktor
     * @param clazz A lekérdezés eredményosztálya
     * @param parameter Where clause paraméterek a lekérdezéshez
     */
    public SimpleFilter(Class<T> clazz, FilterParameter... parameter) {
        this.clazz = clazz;
        this.parameters = parameter;
    }

    /**
     * 
     * @param <X>
     * @param cl
     * @return
     */
    public static <X extends SonrisaJPAEntity<?>> SimpleFilter<X> of(Class<X> cl) {
        return new SimpleFilter<X>(cl);
    }

    /**
     * @param em 
     * @return
     * @see JpaFilter#findQuery
     */
    @Override
    public Query findQuery(EntityManager em) {
        StringBuilder query = new StringBuilder(DEF_INITIAL_CAPACITY);
        query.append("SELECT object(t) FROM ").
                append(clazz.getSimpleName()).
                append(" t ");
        if (parameters != null && parameters.length > 0) {
            query.append(" WHERE ");
            int cnt = 0;
            for (FilterParameter fp : parameters) {
                if (cnt > 0) {
                    query.append(" AND ");
                }
                query.append(property(fp.getProperty()));
                if (fp.getObject() == null) {
                    query.append(" is null ");
                } else {
                    query.append(fp.getOperator()).
                            append(" :").append("param").append(cnt);
                }
                cnt++;
            }
        }
        if (sort != null) {
            query.append(" ORDER BY t.");
            query.append(sort);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(query.toString());
        }
        return em.createQuery(query.toString());
    }

    /**
     * 
     * @param em
     * @return
     */
    @Override
    public Query countQuery(EntityManager em) {
        StringBuilder query = new StringBuilder(DEF_INITIAL_CAPACITY);
        query.append("SELECT count(t) FROM ").
                append(clazz.getSimpleName()).
                append(" t ");
        if (parameters != null && parameters.length > 0) {
            query.append(" WHERE ");
            int cnt = 0;
            for (FilterParameter fp : parameters) {
                if (cnt > 0) {
                    query.append(" AND ");
                }
                fp.append(query, cnt);
                cnt++;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(query.toString());
        }
        return em.createQuery(query.toString());
    }

    /**
     * A paraméterek beállítása. A DAO hívja, nem publikus API.
     * @param query
     */
    @Override
    public void setParameters(Query query) {
        int cnt = 0;
        for (FilterParameter fp : parameters) {
            fp.setParameter(query, cnt);
            cnt++;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(fp.toString());
            }

        }
    }

    /**
     * Az átadott paraméter hozzáadása a where clause paraméterekhez
     * @param fp
     * @return chaining
     */
    public SimpleFilter<T> addParameter(FilterParameter fp) {
        parameters = Arrays.copyOf(parameters, parameters.length + 1);
        parameters[parameters.length - 1] = fp;
        return this;
    }

    /**
     *
     * @param property
     * @param object
     * @return
     */
    public SimpleFilter<T> addParameter(String property, Object object) {
        addParameter(new FilterParameter(property, object));
        return this;
    }

    /**
     * Egy where clause elem hozzáadása
     * @param property
     * @param object
     * @param operator
     * @return
     */
    public SimpleFilter<T> addParameter(String property, Object object, String operator) {
        addParameter(new FilterParameter(property, object, operator));
        return this;
    }

    /**
     * A query opcionális sort paramétere.
     *
     * Példa:
     * "nev"
     * "tipus DESC"
     *
     * @param sort
     * @return chaining
     */
    public SimpleFilter<T> setSort(String sort) {
        this.sort = sort;
        return this;
    }

    /**
     * A lower(column) jellegu property-t lower(t.column)-ra csereli
     * @param property
     * @return
     */
    private String property(String property) {
        int idx = property.indexOf('(');
        if (idx < 0) {
            return "t." + property;
        }
        return property.substring(0, idx + 1) + "t." + property.substring(idx + 1);
    }
}
