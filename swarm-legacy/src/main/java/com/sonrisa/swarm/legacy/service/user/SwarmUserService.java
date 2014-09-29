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

package com.sonrisa.swarm.legacy.service.user;

import com.sonrisa.swarm.model.user.SwarmUser;

/**
 * User details service uses Spring Security to return the current {@link SwarmUser}
 */
public interface SwarmUserService {
    
    /**
     * Get the current user or <i>null</i> if not authenticated
     *
     * @return The current {@link SwarmUser}
     */
    SwarmUser getCurrentLogin();

    /**
     * Check if a user is authenticated
     *
     * @return <code>true</code> if the user is authenticated, <code>false</code> otherwise
     */
    boolean isAuthenticated();
}
