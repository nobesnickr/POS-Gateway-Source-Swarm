/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.legacy.dao;

import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;
import org.springframework.stereotype.Repository;

/**
 * DAO class of invoices.  
 *
 * @author joe
 */
@Repository
public class InvoiceDao extends BaseJpaDao<Long, InvoiceEntity> {

    public InvoiceDao() {
        super(InvoiceEntity.class);
    }
    

    /**
     * Retrieves an invoice by its store and foreign ID.
     * 
     * @param store
     * @param foreignId
     * @return 
     */
    public InvoiceEntity findByStoreAndForeignId(final Long storeId, final Long foreignId){
        SimpleFilter<InvoiceEntity> filter = new SimpleFilter<InvoiceEntity>(InvoiceEntity.class, 
                new FilterParameter("store.id", storeId),
                new FilterParameter("lsInvoiceId", foreignId));                              
        
        return findSingleEntity(filter);
    }
    
}
