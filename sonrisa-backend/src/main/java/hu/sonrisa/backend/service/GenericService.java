package hu.sonrisa.backend.service;

import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import java.io.Serializable;
import java.util.List;

/**
 * Pageble service
 *
 * @author János
 */
public interface GenericService<U extends Serializable, T extends SonrisaJPAEntity<U>> {

    /**
     * Find an audit log event by id
     *
     * @param id
     * @return
     */
    T find(U id);

    /**
     * Count how many audit log events match given filter
     *
     * @param filter
     * @return
     */
    long count(ProviderJpaFilter<T> filter);

    /**
     * Return iterator to audit log events matching given filter
     *
     * @param filter
     * @param first
     * @param count
     * @return
     */
    List<T> find(JpaFilter<T> filter, long first, long count);

    /**
     *
     */
    T findSingle(JpaFilter<T> filter);
}
