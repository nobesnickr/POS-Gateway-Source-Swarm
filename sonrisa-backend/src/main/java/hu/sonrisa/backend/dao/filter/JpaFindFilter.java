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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 * K11Entity háttérrel rendelkező domain objektumok keresését lehetővé tevő
 * osztályok őse.
 *
 * Lehetővé teszi a keresés által visszaadott lista sorrendjének beállítását.
 *
 * NEM ÁLTALÁNOS CÉLÚ OSZTÁLY - 1-1 FELÜLETI SZŰRÉSI LEHETŐSÉG KONKRÉT
 * LEKEZELÉSÉRE KÉSZÜLHET LESZÁRMAZOTT!
 *
 * Ne használtd dao-ban, service-ben, logikában - csak és kizárólag a weben
 * datatable filter-ek backing beanjeként!
 *
 * Factory metódussal gyárt az entity dao számára használható ProviderJpaFilter
 * objektumot.
 *
 * TODO: áthelyezni a web projektbe
 *
 * @param <T>
 * @author cserepj
 */
public abstract class JpaFindFilter<T extends SonrisaJPAEntity<?>> extends AbstractProviderJpaFilter implements Serializable {

    private final static String ENTITY_NAME = "target";
    /**
     * the entity name used in queries
     */
    private String sort;
    private boolean ascending;
    private final List<JpaOrderByParameter> extraSort = new ArrayList<JpaOrderByParameter>();
    /**
     * the class of the queried objects
     */
    protected Class<T> clazz;

    /**
     *
     * @param clazz
     */
    public JpaFindFilter(Class<T> clazz) {
        this(clazz, ENTITY_NAME);
    }

    /**
     *
     * @param clazz
     * @param entityName
     */
    public JpaFindFilter(Class<T> clazz, String entityName) {
        super(entityName);
        this.clazz = clazz;
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
            str.append(entityName).append(".").append(attrKey).append(" = :").append(attrKey.replace('.', '_'));
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
                str.append(entityName).append(".").append(attrKey).append(" = :").append(attrKey.replace('.', '_'));
                where = false;
            }
        }
        return where;
    }

    protected boolean addInClause(StringBuilder str, String attrKey, Object object, boolean where) {
        if (object != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE " : " AND ");
                str.append(entityName).append(".").append(attrKey).append(" IN :").append(attrKey.replace('.', '_'));
                where = false;
            }
        }
        return where;
    }

    protected boolean addIsClause(StringBuilder str, String attrKey, Object object, boolean is, boolean where) {
        if (object != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE " : " AND ");
                str.append(entityName).append(".").append(attrKey).append(is ? " IS NULL " : " = :" + attrKey.replace('.', '_'));
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
                str.append(entityName).append(".").append(attrKey).append(relation).append(" :").append(attrKey.replace('.', '_'));
                where = false;
            }
        }
        return where;
    }

    protected boolean addDateDayClause(StringBuilder str, String attrKey, String pre, String post, Date date, boolean where) {
        if (date != null && attrKey != null) {
            str.append(where ? " WHERE " : " AND ");
            str.append(entityName).append(".").append(attrKey).append(" BETWEEN ").append(" :")
                    .append(pre.replace('.', '_')).append(" AND").append(" :").append(post.replace('.', '_'));
            where = false;
        }
        return where;
    }

    protected boolean addDateOrNullClause(StringBuilder str, String attrKey, Date date, String relation, boolean where) {
        if (date != null) {
            if (attrKey != null) {
                str.append(where ? " WHERE (" : " AND (");
                str.append(entityName).append(".").append(attrKey).append(relation).append(" :").append(attrKey.replace('.', '_'));
                str.append(" OR ").append(entityName).append(".").append(attrKey).append(" IS NULL ");
                str.append(") ");
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
                str.append(" lower(").append(entityName).append(".").append(attrKey).append(") like :").append(attrKey.replace('.', '_'));
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
            q.setParameter(attr.replace('.', '_'), object);
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
            q.setParameter(attr.replace('.', '_'), object.toLowerCase().replace('*', '%') + '%');
        }
    }

    /**
     *
     * @param str
     */
    protected void createFromClause(StringBuilder str) {
        str.append(" FROM ").append(clazz.getSimpleName()).append(" ").append(entityName).append(" ");
    }

    /**
     *
     * @param str
     */
    public abstract void createWhereClause(StringBuilder str);

    /**
     *
     * @param query
     */
    public abstract void setParameters(Query query);

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    /**
     *
     * @return
     */
    public String getSort() {
        return sort;
    }

    /**
     *
     * @param sort
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     *
     * @return
     */
    public List<JpaOrderByParameter> getExtraSort() {
        return extraSort;
    }
}
