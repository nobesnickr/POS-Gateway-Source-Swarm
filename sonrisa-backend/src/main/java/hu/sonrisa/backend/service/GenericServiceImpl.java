package hu.sonrisa.backend.service;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author János
 */
public abstract class GenericServiceImpl<U extends Serializable, T extends SonrisaJPAEntity<U>, X extends BaseJpaDao<U, T>> implements GenericService<U, T> {

    private X dao;

    public GenericServiceImpl(X dao) {
        this.dao = dao;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public T find(U id) {
        return dao.findById(id);
    }

    /**
     *
     * @param filter
     * @return
     */
    @Override
    public long count(ProviderJpaFilter<T> filter) {
        return dao.count(filter);
    }

    /**
     *
     * @param filter
     * @param first
     * @param count
     * @return
     */
    @Override
    public List<T> find(JpaFilter<T> filter, long first, long count) {
        return dao.find(filter, first, count);
    }

    public T findSingle(JpaFilter<T> filter) {
        return dao.findSingleEntity(filter);
    }
}
