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
package hu.sonrisa.backend.dao;

import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * @param <X>
 * @param <T>
 * @author cserepj
 */
public abstract class BaseJpaDao<X extends Serializable, T extends SonrisaJPAEntity<X>> implements BaseDaoInterface<X, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJpaDao.class);
    private Class<T> clazz;
    @PersistenceContext()
    private EntityManager entityManager;

    /**
     *
     * @param clazz
     */
    public BaseJpaDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     *
     * @return
     */
    protected Class<T> getEntityClass() {
        return clazz;
    }

    /**
     *
     * @param filter
     * @return
     */
    @Override
    public long count(ProviderJpaFilter<T> filter) {
        Query cq = filter.countQuery(entityManager);
        filter.setParameters(cq);
        Object o = cq.getSingleResult();
        Long l;
        if (o instanceof List) {
            l = (Long) ((List) o).get(0);
        } else {
            l = (Long) o;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Count query result: " + l);
        }
        return l;
    }

    /**
     *
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> find(JpaFilter<T> filter) {
        Query q = filter.findQuery(entityManager);
        filter.setParameters(q);
        List<T> ret = q.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Result size: " + ret.size());
        }
        return ret;
    }

    /**
     *
     * @param filter
     * @param start
     * @param max
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> find(JpaFilter<T> filter, long start, long max) {
        Query q = filter.findQuery(entityManager);
        q.setFirstResult((int) start);
        if (max > 0) {
            q.setMaxResults((int) max);
        }
        filter.setParameters(q);
        List<T> ret = q.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Result size: " + ret.size());
        }
        return ret;
    }

    /**
     *
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public T findSingleEntity(JpaFilter<T> filter) {
        Query q = filter.findQuery(entityManager);
        filter.setParameters(q);
        try {
            q.setMaxResults(1);
            return (T) q.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("NoResult");
            return null;
        } catch (javax.persistence.NonUniqueResultException ex) {
            LOGGER.warn("NonUniqueResult", ex);
            return null;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public T load(X id) {
        return entityManager.find(getEntityClass(), id);
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public T merge(T object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Merging object: " + object);
        }
        return entityManager.merge(object);
    }

    /**
     *
     * @param object
     */
    @Override
    public void remove(T object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing object: " + object);
        }
        entityManager.remove(object);
    }

    /**
     *
     * @param object
     */
    @Override
    public void persist(T object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Persisting object: " + object);
        }
        entityManager.persist(object);
    }

    /**
     *
     */
    @Override
    public void flush() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Flushing");
        }
        entityManager.flush();
    }

    /**
     *
     * @return
     */
    @Override
    public List<T> findAll() {
        return find(SimpleFilter.of(getEntityClass()));
    }

    /**
     *
     * @param persistenceID
     * @return
     */
    @Override
    public T findById(X persistenceID) {
        if (persistenceID == null) {
            return null;
        }
        try {
            T byId = entityManager.find(getEntityClass(), persistenceID);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("findById: " + persistenceID + " class: " + getEntityClass().getSimpleName());
            }
            return byId;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param entity
     */
    @Override
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    /**
     *
     * @return
     */
    public final EntityManager getEntityManager() {
        return entityManager;
    }
}
