/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Spring profile initializer class.
 * At startup it sets the active spring profile to "production" if no other profiles are already set.
 *
 * @author joe
 */
public class SwarmSpringConfigInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    @Override
    public void initialize(final ConfigurableWebApplicationContext applicationContext) {
        final String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0) {
            applicationContext.getEnvironment().setActiveProfiles("production");
        }
    }
}
