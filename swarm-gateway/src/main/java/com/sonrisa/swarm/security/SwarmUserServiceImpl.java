package com.sonrisa.swarm.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.service.user.SwarmUserService;
import com.sonrisa.swarm.model.user.SwarmUser;

/**
 * Utility class for Spring Security.
 */
@Service
public class SwarmUserServiceImpl implements SwarmUserService {

    /**
     * {@inheritDoc}
     */
    @Override
    public SwarmUser getCurrentLogin() {
        if (isAuthenticated()) {
            return ((SwarmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSwarmUser();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SwarmUserDetails;
    }
}
