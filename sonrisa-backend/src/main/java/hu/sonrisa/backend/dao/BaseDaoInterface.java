/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
import java.io.Serializable;
import java.util.List;

/**
 * Alap dao interface
 *
 * @author dobyman
 * @param <X>
 * @param <E>
 */
public interface BaseDaoInterface<X extends Serializable, E extends SonrisaJPAEntity<?>> {

    /**
     *
     * @param filter
     * @param start
     * @param max
     * @return
     */
    @SuppressWarnings(value = "unchecked")
    List<E> find(JpaFilter<E> filter, long start, long max);

    /**
     *
     * @param filter
     * @return
     */
    long count(ProviderJpaFilter<E> filter);

    /**
     *
     * @return
     */
    List<E> findAll();

    /**
     *
     * @param persistenceID
     * @return
     */
    E findById(X persistenceID);

    /**
     *
     * @param filter
     * @return
     */
    @SuppressWarnings(value = "unchecked")
    E findSingleEntity(JpaFilter<E> filter);

    /**
     *
     */
    void flush();

    /**
     *
     * @param object
     * @return
     */
    E merge(E object);

    /**
     *
     * @param object
     */
    void persist(E object);

    /**
     *
     * @param object
     */
    void remove(E object);

    /**
     * Frissíti az adatbázisból a memóriában lévő entitást.
     *
     * @param object
     */
    void refresh(E object);
}
