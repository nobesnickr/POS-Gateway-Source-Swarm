/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonrisa.swarm.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Salt source for the password hashing.
 *
 * @author Péter Brindzik <brindzik.peter@openminds.hu>
 */
public class ExpressionEngineSaltSource implements org.springframework.security.authentication.dao.SaltSource {

    @Override
    public Object getSalt(UserDetails user) {
        if (user instanceof SwarmUserDetails) {
            return ((SwarmUserDetails) user).getSalt();
        } else {
            throw new RuntimeException("Unsupported user details type: " + user.getClass().getName());
        }
    }

}
