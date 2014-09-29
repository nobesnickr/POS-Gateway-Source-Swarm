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

import com.sonrisa.swarm.admin.service.BaseStatusService;

/**
 * Filter entity which enables filtering, ordering, etc
 * for the {@link StoreStatus} entities returned by the {@link BaseStatusService}
 * 
 * @author Barnabas
 */
public class StatusQueryEntity extends BaseStatusQueryEntity {

    /**
     * Filter for stores with matching active value, null indicates that no filtering should apply
     */
    private Boolean active = null;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "StatusQueryEntity [getActive()=" + getActive() + ", getStoreId()=" + getStoreId() + ", getSkip()="
                + getSkip() + ", getTake()=" + getTake() + ", getOrderBy()=" + getOrderBy() + ", getOrderDir()="
                + getOrderDir() + ", getApi()=" + getApi() + ", getStatus()=" + getStatus() + "]";
    }
}