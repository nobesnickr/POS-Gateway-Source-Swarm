/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonrisa.swarm.security.users.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sonrisa.swarm.model.user.SwarmUser;
import com.sonrisa.swarm.security.users.UserRepository;

/**
 *
 * @author Péter Brindzik <brindzik.peter@openminds.hu>
 */
@Repository
public class ExpressionEngineUserRepository implements UserRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("userDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public SwarmUser getUser(String username) {
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT m.username, m.password, m.salt, m.screen_name, g.can_access_admin  FROM exp_members m ")
             .append("JOIN exp_member_groups g ON g.group_id = m.group_id ")
             .append("WHERE username = ?");
        
        return jdbcTemplate.queryForObject(query.toString(), ROW_MAPPER, username);
    }

    public static final RowMapper<SwarmUser> ROW_MAPPER = new RowMapper<SwarmUser>() {
        @Override
        public SwarmUser mapRow(ResultSet rs, int i) throws SQLException {
            SwarmUser result = new SwarmUser();
            result.setUsername(rs.getString("username"));
            result.setPassword(rs.getString("password"));
            result.setSalt(rs.getString("salt"));
            result.setCanAccessAdmin(rs.getString("can_access_admin"));
            result.setScreenName(rs.getString("screen_name"));
            return result;
        }

    };

}
