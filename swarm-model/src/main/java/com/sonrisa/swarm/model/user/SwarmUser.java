/*
 * To change this l

icense header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonrisa.swarm.model.user;

import java.io.Serializable;

/**
 * This class represents a users.
 *  
 * @author Péter Brindzik <brindzik.peter@openminds.hu>
 */
public class SwarmUser implements Serializable {

    /** Username */
    private String username;
    
    /** Password */
    private String password;
    
    /** Salt for password */
    private String salt;
    
    /** Derived from member groups, indicates whether user can access administration pages */
    private boolean canAccessAdmin;
    
    /** Screen name, human-friendly version of username */ 
    private String screenName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean canAccessAdmin() {
        return canAccessAdmin;
    }

    public void setCanAccessAdmin(boolean canAccessAdmin) {
        this.canAccessAdmin = canAccessAdmin;
    }

    public void setCanAccessAdmin(String canAccessAdminString) {
        if("y".equalsIgnoreCase(canAccessAdminString)){
            this.canAccessAdmin = true;
        } else {
            this.canAccessAdmin = Boolean.parseBoolean(canAccessAdminString);
        }
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public String toString() {
        return "SwarmUser [username=" + username + ", canAccessAdmin=" + canAccessAdmin + ", screenName=" + screenName
                + "]";
    }
}
