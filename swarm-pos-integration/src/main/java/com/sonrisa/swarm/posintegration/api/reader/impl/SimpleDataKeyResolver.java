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

package com.sonrisa.swarm.posintegration.api.reader.impl;


import org.springframework.util.StringUtils;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Implementation which always returns the same dataKey, no matter what the command is
 * @param <T> 
 */
public class SimpleDataKeyResolver<T extends SwarmStore> implements ExternalDataKeyResolver<T> { 

    /**
     * Return value of {@link SimpleDataKeyResolver#getDataKey(ExternalCommand)}
     */
    private ExternalDTOPath path;
    
    /**
     * Data key resolver, expecting data as top level JSON item with given <i>dataKey</i>
     * @param dataKey
     */
    public SimpleDataKeyResolver(String dataKey) {
        if(StringUtils.isEmpty(dataKey)){
            throw new IllegalArgumentException("dataKey can't be null");
        }
        this.path = new ExternalDTOPath(dataKey);
    }
    
    /**
     * Data key resolver, expecting data at <i>path</i> path.
     * @param dataKey
     */
    public SimpleDataKeyResolver(ExternalDTOPath path) {
        this.path = path;
    }

    /**
     * Always returns the same dataKey, no matter what the command is
     */
    @Override
    public ExternalDTOPath getDataKey(ExternalCommand<T> command) {
        return this.path;
    }
}
