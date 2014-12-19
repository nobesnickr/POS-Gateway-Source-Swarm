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

import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.entity.XmlWrappedEntity;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.model.FingerPrinted;
import hu.sonrisa.backend.model.PersistenceAware;
import hu.sonrisa.backend.model.PersistenceAwareBase;
import hu.sonrisa.backend.model.StatusAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * CRUD funkcionalitás wrapper XML mappelt domain objektum és Entity között.
 *
 * @author dobyman
 * @param <X>
 * @param <T>
 * @param <U>
 */
public class WrapperCrud<X extends Serializable, T extends PersistenceAware<X>, U extends XmlWrappedEntity<X, T>> {

    private static final Logger LOGGER = LoggerFactory.
            getLogger(WrapperCrud.class);
    /**
     *
     */
    protected Class<U> entityClass;
    /**
     *
     */
    protected BaseDaoInterface<X, U> dao;

    /**
     * Konstruktor
     */
    protected WrapperCrud() {
    }

    /**
     * Konstruktor
     * <p/>
     * @param entityClass
     * @param dao
     */
    public WrapperCrud(Class<U> entityClass, BaseDaoInterface<X, U> dao) {
        this.entityClass = entityClass;
        this.dao = dao;
    }

    /**
     * Domain objektum mentése wrapper entity-n keresztül
     * <p/>
     * @param obj
     * @param userId
     * @param fullName
     * @return
     * @throws JpaVersionException
     */
    public U save(T obj, String userId, String fullName) throws
            JpaVersionException {
        return internalSave(obj, userId, fullName);
    }

    private U internalSave(T obj, String userId, String fullName) throws
            JpaVersionException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Saving object: " + obj);
        }
        X persistenceID = obj.getPersistenceID();
        U entity = null;
        if (obj instanceof FingerPrinted) {
            FingerPrinted f = (FingerPrinted) obj;
            f.setModifiedBy(userId);
            f.setModifiedAt(new Date());
        }
        if (persistenceID != null) {
            entity = dao.findById(persistenceID);
        }
        if (entity == null) {
            try {
                if (obj instanceof FingerPrinted) {
                    FingerPrinted f = (FingerPrinted) obj;
                    f.setCreatedBy(userId);
                    f.setCreatedAt(new Date());
                }
                entity = newEntityInstance(obj);
                entity.setWrappedObject(obj);
                dao.persist(entity);
                wrapObject(obj, entity);
                dao.flush();
                if (obj.getPersistenceID() == null) {
                    obj.setPersistenceID(entity.getId());
                }
                if (obj instanceof StatusAware && userId != null) {
                    handleStatuszChange(obj, entity, userId, fullName);
                }
            } catch (InstantiationException ex) {
                LOGGER.error("This should never happen.", ex);
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                LOGGER.error("This should never happen.", ex);
                throw new RuntimeException(ex);
            }
        } else {
            //#291: ellenorizzuk, hogy nem volt-e  modositva az entity ahhoz kepest,
            //amibol a wrapped objektum szarmazik
            if (obj instanceof PersistenceAwareBase
                    && !entity.getVersion().equals(((PersistenceAwareBase) obj).
                    getJPAVersion())) {
                throw new JpaVersionException("verzió nem egyezik: entity ="
                        + entity.getVersion() + " obj ="
                        + ((PersistenceAwareBase) obj).getJPAVersion());
            }

            if (obj instanceof StatusAware && userId != null) {
                Enum ujStatusz = ((StatusAware) obj).getStatusz();
                Enum regiStatusz = ((StatusAware) entity).getStatusz();
                if (!regiStatusz.equals(ujStatusz)) {
                    handleStatuszChange(obj, entity, userId, fullName);
                }
            }
            entity.setWrappedObject(obj);
            dao.flush();
            wrapObject(obj, entity);
        }
        // írjuk vissza a változott adatokat
        dao.flush();
        entity.onUnWrap(obj);
        return entity;
    }

    /**
     * A WrapperCrud leszármazott osztályának ebben a metódusban van még egy
     * utolsó lehetősége arra, hogy az entity adatain módosítson a domain
     * objektum egyes dolgai alapján. Az entity onUnWrap metódusával ellentétben
     * ebben a metódusban elérhető a service/logika réteg. Ezért olyan logikát
     * érdemes itt megvalósítani amihez ezek elérése szükséges.
     *
     * @param object
     * @param entity
     */
    protected void wrapObject(T object, U entity) {
    }

    /**
     *
     * @param obj
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected U newEntityInstance(T obj) throws InstantiationException,
            IllegalAccessException {
        return entityClass.newInstance();
    }

    /**
     * Minden entitást megkeres.
     *
     * @return
     */
    public List<U> findAllEntity() {
        return dao.findAll();
    }

    /**
     * Minden wrappolt domain objektumot megkeres
     * <p/>
     * @return
     */
    public List<T> findAll() {
        List<T> res = new ArrayList<T>();
        for (U we : dao.findAll()) {
            res.add(unwrap(we));
        }
        return res;
    }

    /**
     * Id alapján megkeres egy domain objektumot
     * <p/>
     * @param id
     * @return
     */
    public T find(X id) {
        U entity = dao.findById(id);
        return unwrap(entity);
    }

    /**
     *
     * @param filter
     * @param start
     * @param max
     * @return
     */
    public List<U> findEntity(JpaFilter<U> filter, int start, int max) {
        return dao.find(filter, start, max);
    }

    /**
     *
     * @param filter
     * @param start
     * @param max
     * @return
     */
    public List<T> find(JpaFilter<U> filter, int start, int max) {
        List<U> l = findEntity(filter, start, max);
        List<T> ret = new ArrayList<T>(l.size());
        for (U u : l) {
            LOGGER.debug("Unwrapping: " + u.getMegnevezes());
            ret.add(unwrap(u));
        }
        return ret;
    }

    /**
     *
     * @param filter
     * @return
     */
    public long count(ProviderJpaFilter<U> filter) {
        return dao.count(filter);
    }

    /**
     *
     * @param obj
     */
    public void remove(T obj) {
        if (obj.getPersistenceID() != null) {
            U entity = dao.findById(obj.getPersistenceID());
            dao.remove(entity);
        }
    }

    /**
     *
     * @param obj
     */
    public void remove(U obj) {
        if (obj.getId() != null) {
            U entity = dao.findById(obj.getId());
            dao.remove(entity);
        }
    }

    /**
     *
     * @param uuid
     */
    public void remove(X uuid) {
        U entity = dao.findById(uuid);
        dao.remove(entity);
    }

    /**
     *
     * @param coll
     * @return
     */
    public List<T> unwrap(Iterable<U> coll) {
        List<T> res = new ArrayList<T>();
        for (U ent : coll) {
            res.add(unwrap(ent));
        }
        return res;
    }

    /**
     *
     * @param coll
     * @return
     */
    public Iterable<T> unwrapIterable(final Iterable<U> coll) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                final Iterator<U> it = coll.iterator();

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public T next() {
                        return unwrap(it.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException(BackendExceptionConstants.BEND_00004);
                    }
                };
            }
        };
    }

    /**
     *
     * @param ent
     * @return
     */
    protected T unwrap(U ent) {
        if (ent == null) {
            return null;
        }
        return ent.getWrappedObject();
    }

    /**
     * meghivja a Dao.flush()-t
     */
    public void flush() {
        dao.flush();
    }

    /**
     *
     * @param id
     * @return
     */
    public U findEntity(X id) {
        return dao.findById(id);
    }

    /**
     *
     * @param obj
     * @param entity
     * @param userId
     * @param fullName
     */
    protected void handleStatuszChange(T obj, U entity, String userId, String fullName) {
        // implement in subclass
    }
}
