/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sonrisa.backend.versionedobject;

import hu.sonrisa.backend.cache.ConcurrentCacheHashMap;
import hu.sonrisa.backend.dao.BaseDaoInterface;
import hu.sonrisa.backend.dao.JpaVersionException;
import hu.sonrisa.backend.dao.WrapperCrud;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import hu.sonrisa.backend.jaxb.JAXBUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author joe
 */
public abstract class AbstractVersionedObjectLogic <T extends VersionedObject, X extends VersionedObjectEntity<T>> 
                    extends WrapperCrud<String, T, X> {

    protected Map<String, T> cache = new ConcurrentCacheHashMap<String, T>();

    /**
     * 
     * @param clazz
     * @param dao
     */
    public AbstractVersionedObjectLogic(Class clazz, BaseDaoInterface<String, X> dao) {
        super(clazz, dao);
    }

    @Override
    public T find(String id) {
        if (id == null) {
            return null;
        }

        T obj = null;
        if (cache.containsKey(id)) {
            obj = cache.get(id);
        } else {
            obj = super.find(id);
            if (obj != null) {
                cache.put(obj.getId(), obj);
            }
        }
        return obj;
    }

    @Override
    public List<T> find(JpaFilter<X> filter, int start, int max) {
        List<X> entities = super.findEntity(filter, start, max);
        List<T> ret = new ArrayList<T>(entities.size());
        for (X entity : entities) {
            ret.add(find(entity.getId()));
        }
        return ret;
    }

    /**
     * @param uuid
     * @param deleted 
     * @return Visszaadja az adott azonosítóhoz tartozó verziózott objektum összes
     *         verzióját.
     */
    public List<X> findVerziokByUUID(final String uuid, final Boolean deleted) {
        X ent = (X) find(uuid);
        if (ent == null) {
            return Collections.emptyList();
        } else {
            return findVerziokByKod(ent.getKod(), deleted);
        }
    }

    /**
     * @param kod
     * @param deleted 
     * @return Visszaadja az adott típusú és kódú verziózott objektum
     *         összes verzióját.
     */
    public List<X> findVerziokByKod(final String kod, final Boolean deleted) {
        return findEntity(new JpaFilter<X>() {

            @Override
            public Query findQuery(EntityManager em) {
                StringBuilder sb = new StringBuilder();
                sb.append("select v from  ").append(entityClass.getSimpleName()).append(" v ");
                sb.append("where v.kod = :kod ");
                if (deleted != null) {
                    sb.append("and v.deleted = :deleted");
                }
                return em.createQuery(sb.toString());
            }

            @Override
            public void setParameters(Query query) {
                query.setParameter("kod", kod);
                if (deleted != null) {
                    query.setParameter("deleted", deleted);
                }
            }
        }, 0, -1);
    }

    /**
     * @param kod
     * @return Visszaadja az adott típussal és kóddal rendelkező verziózott objektum
     *         legutolsó verzióját.
     */
    public X findLatestVerzioEntity(final String kod) {
        Collection<X> entities = findEntity(new JpaFilter<X>() {

            @Override
            public Query findQuery(EntityManager em) {
                return em.createQuery("select v from  " + entityClass.getSimpleName() + " v "
                        + "where "
                        + "      v.kod = :kod "
                        + " and v.deleted = :deleted "
                        + "order by v.createdAt desc ");
            }

            @Override
            public void setParameters(Query query) {
                query.setParameter("kod", kod);
                query.setParameter("deleted", false);
            }
        }, 0, 1);
        return entities.size() > 0 ? entities.iterator().next() : null;
    }

    /**
     * 
     * @param kod
     * @return
     */
    public T findLatestVerzio(final String kod) {
        X ent = findLatestVerzioEntity(kod);
        if (ent != null) {
            return find(ent.getId());
        } else {
            return null;
        }
    }

    ;

    /**
     * 
     * @param deleted
     * @return
     */
    public List<X> findAllLatest(final Boolean deleted) {
        return dao.find(new JpaFilter<X>() {

            @Override
            public Query findQuery(EntityManager em) {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT object(target) FROM ");
                sb.append(entityClass.getSimpleName()).append(" target ");
                sb.append("WHERE target.createdAt = ");
                sb.append("(SELECT MAX(maxDate.createdAt) FROM ");
                sb.append(entityClass.getSimpleName()).append(" maxDate WHERE maxDate.kod = target.kod");
                if (deleted != null) {
                    sb.append(" and maxDate.deleted = :deleted");
                }
                sb.append(")");
                return em.createQuery(sb.toString());
            }

            @Override
            public void setParameters(Query query) {
                if (deleted != null) {
                    query.setParameter("deleted", deleted);
                }
            }
        }, 0, 0);
    }

    /**
     * Az osszes objektumot visszaadja, a 
     * logikailag torolteket is.
     * 
     * @return 
     */
    @Override
    public List<T> findAll() {
        List<T> unwrappedList = new ArrayList<T>();
        for (X ent : findAllEntity()) {
            unwrappedList.add(find(ent.getId()));
        }
        return unwrappedList;
    }
    
    /**
     * Az osszes aktiv objektumot visszaadja. 
     * Azaz a logikailag torolteket nem.
     * 
     * @return 
     */    
    public List<T> findAllAktiv() {        
        List<X> result = dao.find(new SimpleFilter<X>(entityClass, new FilterParameter("deleted", false)), 0, 0);
        
        List<T> unwrappedList = new ArrayList<T>();
        for (X ent : result) {
            unwrappedList.add(find(ent.getId()));
        }
        return unwrappedList;
        
    }    

    @Override
    public X save(T obj, String userId, String fullName) {
        boolean masolat = find(obj.getPersistenceID()) != null;
        try {
            if (!masolat) {
                // ez egy új objektum
                return super.save(obj, userId, fullName);
            } else {
                T mentendo = JAXBUtil.clone(obj);
                // új verzió egy létező űrlapból
                mentendo.setSzuloId(mentendo.getPersistenceID());
                mentendo.setId(UUID.randomUUID().toString());
                return super.save(mentendo, userId, fullName);
            }
        } catch (JpaVersionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Cache törlése
     */
    public void clearCache() { 
        cache.clear(); 
    }
    
    /**
     * 
     * @param deleted
     * @return 
     */
    List<X> findAllLatestObject(Boolean deleted) {
        return (List<X>) unwrap(findAllLatest(deleted));
    }
    
}
