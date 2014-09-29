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

package com.sonrisa.swarm.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonrisa.swarm.model.user.SwarmUser;

/**
 * User entity 
 */
public class UserEntity {

    @JsonProperty("userName")
    private String userName;
    
    @JsonProperty("screenName")
    private String screenName;
    
    @JsonProperty("role")
    private String role;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static UserEntity fromSwarmUser(SwarmUser user){
        UserEntity entity = new UserEntity();
        entity.setUserName(user.getUsername());
        entity.setScreenName(user.getScreenName());
        entity.setRole(user.canAccessAdmin() ? "ROLE_ADMIN" : "ROLE_USER");
        return entity;
    }
}

