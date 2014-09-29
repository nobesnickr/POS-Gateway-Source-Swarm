package com.sonrisa.swarm.security;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sonrisa.swarm.model.user.SwarmUser;
import com.sonrisa.swarm.security.users.UserRepository;

/**
 * Authenticate a user from the database.
 */
public class ExpressionEngindeUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionEngindeUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        LOGGER.info("Authenticating {}", login);
        SwarmUser user = userRepository.getUser(login);
        if (user == null) {
            throw new UsernameNotFoundException("User " + login + " was not found in the database");
        }

        Collection<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        if(user.canAccessAdmin()){
            grantedAuthorities.addAll(AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }

        return new SwarmUserDetails(user, login, user.getPassword(), grantedAuthorities);
    }
}
