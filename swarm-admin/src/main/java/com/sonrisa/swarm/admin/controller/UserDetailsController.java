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

package com.sonrisa.swarm.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.admin.model.UserEntity;
import com.sonrisa.swarm.legacy.service.user.SwarmUserService;
import com.sonrisa.swarm.model.user.SwarmUser;

/**
 * Controller for accessing user details
 */
@Controller
public class UserDetailsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsController.class);
    
    @Autowired
    private SwarmUserService userService;
    
    /**
     * Gets the user details
     */
    @Secured("ROLE_USER")
    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public @ResponseBody ResponseEntity<UserEntity> details() {

        SwarmUser user = userService.getCurrentLogin();
        LOGGER.info("User details fetch by {}", user);

        return new ResponseEntity<UserEntity>(UserEntity.fromSwarmUser(user), HttpStatus.OK);
    }
    
    /**
     * Gets the admin user details
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.GET, value = "/admin/user")
    public @ResponseBody ResponseEntity<UserEntity> adminDetails() {

        SwarmUser user = userService.getCurrentLogin();
        LOGGER.info("Admin details fetch by {}", user);

        return new ResponseEntity<UserEntity>(UserEntity.fromSwarmUser(user), HttpStatus.OK);
    }
}
