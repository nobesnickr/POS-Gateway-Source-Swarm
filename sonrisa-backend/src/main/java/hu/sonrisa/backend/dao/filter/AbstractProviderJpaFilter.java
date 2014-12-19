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
import java.io.Serializable;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * A wicket-es DataProvider API-hoz idomuló QueryBuilder jellegű osztály
 *
 * @param <T>
 * @author cserepj
 */
public abstract class AbstractProviderJpaFilter<T extends SonrisaJPAEntity<?>> implements SortableProviderJpaFilter<T>, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProviderJpaFilter.class);

    /**
     * A WHERE clause összeállítását végző metódus
     *
     * @param str a StringBuilder amihez appendelnünk kell
     */
    protected abstract void createWhereClause(StringBuilder str);

    /**
     * A FROM clause összeállítását végző metódus
     *
     * @param str a StringBUilder amihez appendelnünk kell
     */
    protected abstract void createFromClause(StringBuilder str);
    /**
     *
     */
    protected String entityName;

    /**
     *
     * @param entityName
     */
    public AbstractProviderJpaFilter(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public final Query findQuery(EntityManager em) {
        return buildFindQuery(em, false, getSort(), isAscending());
    }

    /**
     *
     * @param em
     * @return
     */
    @Override
    public final Query countQuery(EntityManager em) {
        return buildFindQuery(em, true, null, false);
    }

    /**
     *
     * @param em
     * @param count
     * @param sort
     * @param asc
     * @return
     */
    protected final Query buildFindQuery(EntityManager em, boolean count, String sort,
            boolean asc) {

        StringBuilder ejbql = new StringBuilder();
        if (count) {
            createCountQuery(ejbql);
        } else {
            createSelectQuery(ejbql);
        }
        createFromClause(ejbql);
        createWhereClause(ejbql);
        if (!count) {
            createOrderBy(ejbql, sort, asc);
        }
        String q = ejbql.toString();
        logger.debug("Constructed query: " + q);
        return em.createQuery(q);
    }

    /**
     *
     * @param ejbql
     */
    protected void createCountQuery(StringBuilder ejbql) {
        ejbql.append("select count(" + entityName + ") ");
    }

    /**
     *
     * @param ejbql
     */
    protected void createSelectQuery(StringBuilder ejbql) {
        ejbql.append("select object(" + entityName + ") ");
    }

    /**
     *
     * @param str
     * @param sort
     * @param asc
     */
    protected void createOrderBy(StringBuilder str, String sort, boolean asc) {
        if (sort != null || isExtraSort()) {
            str.append(" order by ");
            boolean extraSortSeparatorNeeded = false;
            if (getSort() != null) {
                extraSortSeparatorNeeded = true;
                appendSimpleProperty(getSort(), str);
                str.append(isAscending() ? " asc" : " desc");
            }
            // ha lett tovabbi rendezes attributum megadva
            if (isExtraSort()) {
                for (JpaOrderByParameter param : getExtraSort()) {
                    /**
                     * @see
                     * hu.sonrisa.kgr.k11.core.model.filter.AdatszolgaltatasDefinicioFindFilter
                     * ha van alapban extra rendezesi attributum, de a feluleten
                     * kijelolunk egy oszlopot, ami alapjan akarunk rendezni,
                     * akkor ne legyen ismetelten benne a query-ben
                     *
                     */
                    if (!param.getProperty().equals(getSort())) {
                        str.append(extraSortSeparatorNeeded ? ", " : "");
                        appendSimpleProperty(param.getProperty(), str);
                        str.append(param.isAscending() ? " asc" : " desc");
                        extraSortSeparatorNeeded = true;
                    }
                }
            }
        }
    }
    private static final String PROP_DELIM = ".";

    private void appendSimpleProperty(String property, StringBuilder builder) {
        if (property.indexOf(entityName) < 0) {
            builder.append(entityName).append(PROP_DELIM);
        }
        builder.append(property);
    }

    @Override
    public abstract boolean isAscending();

    @Override
    public abstract String getSort();

    /**
     * Beteszi mindig a sort után az extra sortot, ha az meg van adva.
     *
     * @return
     */
    protected List<JpaOrderByParameter> getExtraSort() {
        return null;
    }

    private boolean isExtraSort() {
        return getExtraSort() != null && !getExtraSort().isEmpty();
    }

    /**
     *
     * @param str
     * @param attrKey
     * @param where
     * @return
     */
    protected boolean addClause(StringBuilder str, String attrKey, boolean where) {
        if (attrKey != null) {
            str.append(where ? " WHERE " : " AND ");
            str.append(entityName).append(".").append(attrKey).append(" = :").append(attrKey);
            where = false;
        }
        return where;
    }

    /**
     *
     * @param str
     * @param attrKey
     * @param object
     * @param where
     * @return
     */
    protected boolean addClause(StringBuilder str, String attrKey, Object object, boolean where) {
        if (object != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE " : " AND ");
                str.append(entityName).append(".").append(attrKey).append(" = :").append(attrKey);
                where = false;
            }
        }
        return where;
    }

    /**
     * Dátum értéket 'between'-nel vizsgáló clause hosszáfűzése a lekérdezéshez.
     *
     * @param str
     * @param attrKey
     * @param date
     * @param relation
     * @param where
     * @return
     */
    protected boolean addDateClause(StringBuilder str, String attrKey, Date date, String relation, boolean where) {
        if (date != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE " : " AND ");
                str.append(entityName).append(".").append(attrKey).append(relation).append(" :").append(attrKey);
                where = false;
            }
        }
        return where;
    }

    /**
     *
     * @param str
     * @param attrKey
     * @param object
     * @param where
     * @return
     */
    protected boolean addLikeClause(StringBuilder str, String attrKey, Object object, boolean where) {
        if (object != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE " : " AND ");
                str.append(" lower(").append(entityName).append(".").append(attrKey).append(") like :").append(attrKey);
                where = false;
            }
        }
        return where;
    }

    /**
     *
     * @param q
     * @param attr
     * @param object
     */
    protected void addParameterIf(Query q, String attr, Object object) {
        if (object != null) {
            q.setParameter(attr, object);
        }
    }

    /**
     *
     * @param q
     * @param attr
     * @param object
     */
    protected void addLikeParameterIf(Query q, String attr, String object) {
        if (object != null) {
            q.setParameter(attr, object.toLowerCase().replace('*', '%') + '%');
        }
    }
}
