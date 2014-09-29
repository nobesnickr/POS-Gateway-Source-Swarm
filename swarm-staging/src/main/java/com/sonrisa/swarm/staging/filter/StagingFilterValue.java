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
package com.sonrisa.swarm.staging.filter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Return value of {@link BaseStagingEntityFilter#approve(com.sonrisa.swarm.model.staging.BaseStageEntity)}
 */
public enum StagingFilterValue {
    
    /**
     *  Approved should be moved to the legacy tables
     */
    APPROVED(0),
    
    /**
     *  Movable should be moved, but flagged as invalid
     */
    MOVABLE_WITH_FLAG(1),
    
    /**
     *  Retainable shouldn't be moved, it should be kept in the staging tables
     */
    RETAINABLE(2);
    
    // Severity marks the filter values, the most severe will be used from many
    private int severity;
    
    private StagingFilterValue(int severity){
        this.severity = severity;
    }
        
    /**
     * @brief Retrieves to most severe filter value from many
     * @param filters
     * @return
     */
    public static StagingFilterValue getMostSevere(List<StagingFilterValue> filters){
        if(filters.size() == 0){
            return APPROVED;
        }
        return Collections.max(filters,new Comparator<StagingFilterValue>(){
            @Override
            public int compare(StagingFilterValue o1, StagingFilterValue o2) {
                return o1.severity - o2.severity;
            }
        });
    }
}
