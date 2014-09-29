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
package com.sonrisa.swarm.admin.service.util;

import java.util.List;

import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;

/**
 * A store's status is determined by collecting "votes" and picking all of the 
 * most severe votes's reason.
 */
public class StatusVoteEntity {

    /**
     * Voted status
     */
    private StoreStatus status;
    
    /**
     * Reason for this vote
     */
    private String reason;

    public StatusVoteEntity(StoreStatus status, String reason) {
        super();
        this.status = status;
        this.reason = reason;
    }
    
    public StatusVoteEntity(StoreStatus status) {
        super();
        this.status = status;
        this.reason = null;
    }
    
    /**
     * Returns the most severe votes, so if there is a single error,
     * it only returns the error.
     */
    public static StoreStatus getMostSevereStatus(StatusVoteEntity[] votes, List<String> outReasons){
             
        // No votes
        if(votes == null || votes.length == 0){
            return StoreStatus.OK;
        }
        
        StoreStatus mostSevere = StoreStatus.OK;
        
        // Search max
        for(StatusVoteEntity vote : votes){
            if(vote.getStatus().ordinal() > mostSevere.ordinal()){
                mostSevere = vote.getStatus();
            }
        }
        
        for(StatusVoteEntity vote : votes){
            if(vote.getStatus() == mostSevere){
                outReasons.add(vote.getReason());
            }
        }
        
        return mostSevere;
    }

    public StoreStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
