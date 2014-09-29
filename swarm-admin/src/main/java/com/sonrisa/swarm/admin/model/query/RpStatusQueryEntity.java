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

package com.sonrisa.swarm.admin.model.query;

import com.sonrisa.swarm.admin.model.RpStatusEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 * Filter entity for {@link RpStatusEntity} which are basically the
 * rows of the <code>stores_rp</code> table. 
 * 
 * @author Barnabas
 */
public class RpStatusQueryEntity extends BaseStatusQueryEntity {

    /**
     * Client's swarm_id, unique per installation
     */
    private String swarmId;
    
    /**
     * When false only stores with {@link RpStoreEntity.RpStoreState#NORMAL} are returned
     */
    private boolean includeAll;

    /**
     * Client's swarm_id, unique per installation
     */
    public String getSwarmId() {
        return swarmId;
    } 

    /**
     * Client's swarm_id, unique per installation
     */
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }

    public boolean getIncludeAll() {
        return includeAll;
    }

    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
    }

    @Override
    public String toString() {
        return "RpStatusQueryEntity [getSwarmId()=" + getSwarmId() + ", getIncludeAll()=" + getIncludeAll()
                + ", getStoreId()=" + getStoreId() + ", getSkip()=" + getSkip() + ", getTake()=" + getTake()
                + ", getOrderBy()=" + getOrderBy() + ", getOrderDir()=" + getOrderDir() + ", getApi()=" + getApi()
                + ", getStatus()=" + getStatus() + "]";
    }
}
