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

import com.sonrisa.swarm.model.staging.BaseStageEntity;

/**
 * Generic interface for filters judging staging entities whether they should
 * be kept in the staging tables or moved to the legacy tables marked
 * with 0 as completed or 1 as completed 
 * 
 * @param <T>
 */
public interface BaseStagingEntityFilter<T extends BaseStageEntity> {
    
    /**
     * Apply filter for the entity
     * 
     * @param entity Entity to validate
     * @return Result of execution, a suggestion for deletion, keeping in staging, e.g.
     */
    StagingFilterValue approve(T entity); 

}
