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
package com.sonrisa.swarm.warehouse.stage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Data store that provides the combined of a CsvDumpDataStore and
 * a StageDataStore
 * 
 * @author sonrisa
 */
@Component
public class StageAndDumpDataWarehouse implements SwarmDataWarehouse {	
    /**
     * Base data store where data is saved and whichis used the
     * get the timestamp variables 
     */
    @Autowired
    private StagingDTOService baseDataStore;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DWTransferable> void save(SwarmStore store, List<? extends T> entities, Class<T> clazz) {
        baseDataStore.saveToStage(store, entities, clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override 
    public DWFilter getFilter(SwarmStore store, Class<? extends DWTransferable> dtoClass) {
        return baseDataStore.getFilter(store, dtoClass);
    }

    @Override
    public String toString() {
        return "StageAndDumpDataWarehouse";
    }
}
