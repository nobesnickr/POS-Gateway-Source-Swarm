/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonrisa.swarm.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.sonrisa.swarm.model.user.SwarmUser;

/**
 * Spring user details for swarm users.
 * 
 * @author Péter Brindzik <brindzik.peter@openminds.hu>
 */
public class SwarmUserDetails extends User {

    private SwarmUser swarmUser;

    public SwarmUserDetails(SwarmUser swarmUser, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.swarmUser = swarmUser;
    }

    public SwarmUserDetails(SwarmUser swarmUser, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.swarmUser = swarmUser;
    }

    public SwarmUser getSwarmUser() {
        return swarmUser;
    }

    public String getSalt() {
        return swarmUser == null ? null : swarmUser.getSalt();
    }

}
