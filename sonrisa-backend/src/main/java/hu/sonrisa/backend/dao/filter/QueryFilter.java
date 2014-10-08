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
package hu.sonrisa.backend.dao.filter;

import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bármilyen egyedi JP QL query lefuttatását lehetővé tevő egyszerű filter.
 *
 * A BaseJpaDao find() vagy findSingleEntity() metódusának lehet átadni.
 *
 * @param <T>
 * @author cserepj
 */
public class QueryFilter<T extends SonrisaJPAEntity> implements JpaFilter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFilter.class);
    private String query;
    private FilterParameter[] parameters;

    /**
     * Konstruktor
     *
     * @param query a futtatni kívánt JP QL query
     * @param parameters átadott paraméterek
     */
    public QueryFilter(String query, FilterParameter... parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    @Override
    public Query findQuery(EntityManager em) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(query);
        }
        return em.createQuery(query);
    }

    /**
     * Felülírható metódus a query számára szükséges paraméterek átadására
     * használható
     *
     * @param query
     */
    @Override
    public void setParameters(Query query) {
        for (FilterParameter fp : parameters) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter: " + fp.getProperty() + " = " + fp.getObject());
            }
            query.setParameter(fp.getProperty(), fp.getObject());
        }
    }

    public void addParameter(FilterParameter filterParameter) {
        int len = parameters.length;
        parameters = Arrays.copyOf(parameters, len + 1);
        parameters[len] = filterParameter;
    }
}
