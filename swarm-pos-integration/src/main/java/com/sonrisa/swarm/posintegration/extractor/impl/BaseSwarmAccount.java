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
package com.sonrisa.swarm.posintegration.extractor.impl;

import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Base implementation of the {@link SwarmStore} interface
 */
public abstract class BaseSwarmAccount implements SwarmStore {

    /**
     * Store ID of the store
     */
    private Long storeId;

    /**
     * Store's name for debugging purposes
     */
    private String storeName;
    
    public BaseSwarmAccount(Long storeId) {
        super();
        this.storeId = storeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseSwarmAccount other = (BaseSwarmAccount) obj;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        } else if (!storeId.equals(other.storeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BaseSwarmAccount [storeId=" + storeId + ", storeName=" + storeName + "]";
    }
}
