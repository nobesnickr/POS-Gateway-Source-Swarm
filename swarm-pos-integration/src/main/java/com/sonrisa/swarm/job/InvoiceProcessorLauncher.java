/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.job;

import org.springframework.batch.item.ItemProcessor;

import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalProcessor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Launcher of {@link ExternalProcessor} for {@link InvoiceEntity}
 * 
 * @author Barnabas
 */
public abstract class InvoiceProcessorLauncher<T extends SwarmStore> implements ItemProcessor<InvoiceEntity, InvoiceEntity>{

    /**
     * Launch the {@link ExternalProcessor} for the {@link InvoiceEntity}
     */
    @Override
    public InvoiceEntity process(InvoiceEntity item) throws Exception {

        T account = createAccount(item.getStore());
        
        getExternalProcessor().processEntity(account, item, getDataWarehouse());
        
        return item;
    }

    /**
     * Returns the data warehouse to write the received information into.
     * 
     * @return 
     */
    protected abstract SwarmDataWarehouse getDataWarehouse();  
    
    /**
     * Get actual processing instance
     */
    protected abstract ExternalProcessor<T, InvoiceEntity> getExternalProcessor();
    
    /**
     * Get account for given store instance
     */
    protected abstract T createAccount(StoreEntity store);
}
