package hu.sonrisa.backend.service;

import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Very thin wrapper around entitymanager, mainly intended to be used by wicket
 * provider and detachable model classes
 *
 * @author János
 */
@Service
public class SonrisaJpaService {
    
    @PersistenceContext
    private EntityManager em;
    private final static Logger LOGGER = LoggerFactory.getLogger(SonrisaJpaService.class);

    /**
     *
     * @param id
     * @return
     */
    public <T extends SonrisaJPAEntity<U>, U extends Serializable> T find(Class<T> cl, U id) {
        return em.find(cl, id);
    }
    
    public <T extends SonrisaJPAEntity<U>, U extends Serializable> long count(ProviderJpaFilter<T> filter) {
        Query cq = filter.countQuery(em);
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
    public <T extends SonrisaJPAEntity<U>, U extends Serializable> List<T> find(JpaFilter<T> filter, long start, long count) {
        Query q = filter.findQuery(em);
        filter.setParameters(q);
        q.setFirstResult((int) start);
        q.setMaxResults((int) count);
        List<T> ret = q.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Result size: " + ret.size());
        }
        return ret;
    }
}
