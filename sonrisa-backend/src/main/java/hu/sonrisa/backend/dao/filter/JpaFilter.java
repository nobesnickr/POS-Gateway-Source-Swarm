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
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Egy JPA QL query kezelésére, létrehozására, futtatásának segítésére
 * szolgáló absztrakció.
 *
 * Ezt az absztrakciót a BaseJpaDao find() metódusai használják.
 *
 * @param <T>
 * @author cserepj
 */
public interface JpaFilter<T extends SonrisaJPAEntity>  {

    /**
     * A metódus legyárt egy Query objektumot az átadott EntityManager segítségével.
     * A query előállítása az implementáló osztály feladata, saját belső állapota alapján.
     * @param em
     * @return
     */
    Query findQuery(EntityManager em);

    /**
     * A Query paramétereinek beállítására szolgáló metódus
     * @param query
     */
    void setParameters(Query query);
}
