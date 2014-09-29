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
package com.sonrisa.swarm.posintegration.extractor.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalProcessor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;

/**
 * Basic class for implementing {@link ExternalProcessor}
 */
public abstract class BaseExternalProcessor<S extends SwarmStore, T extends BaseLegacyEntity> implements ExternalProcessor<S, T> {

    /**
     * Transform transforming {@link ExternalDTO} to internal dto
     */
    @Autowired
    private ExternalDTOTransformer dtoTransformer;

    protected ExternalDTOTransformer getDtoTransformer() {
        return dtoTransformer;
    }

    public void setDtoTransformer(ExternalDTOTransformer dtoTransformer) {
        this.dtoTransformer = dtoTransformer;
    }
}
