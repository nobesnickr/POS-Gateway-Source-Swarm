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

package com.sonrisa.swarm.test.matcher;

import org.mockito.ArgumentMatcher;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Custom matcher for {@link ExternalCommand}
 */
public class ExternalCommandMatcher<T extends SwarmStore> extends ArgumentMatcher<ExternalCommand<T>> {
    /**
     * Matcher matching the parameters or null if not matching any
     */
    private MapContainsMatcher<String, String> parameterMatcher = null;
    
    /**
     * Matcher matching the parameters or null if not matching any
     */
    private MapContainsMatcher<String, String> configMatcher = null;

    /**
     * URI expected, or null if non expected
     */
    private String expectedUri = null;

    /**
     * Account expected or null if non expected
     */
    private SwarmStore expectedAccount = null;

    public ExternalCommandMatcher() {
        // Nothing-to-expect yet
    }

    public ExternalCommandMatcher(String expectedUri) {
        andUri(expectedUri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Object argument) {
        if (!(argument instanceof ExternalCommand<?>)) {
            return false;
        }

        ExternalCommand<?> command = (ExternalCommand<?>) argument;

        // Check that URI matches
        if (this.expectedUri != null && !this.expectedUri.equals(command.getURI())) {
            return false;
        }

        // Check the query parameters match
        if (this.parameterMatcher != null && !this.parameterMatcher.matches(command.getParams())) {
            return false;
        }
        
        // Check the config parameters match
        if (this.configMatcher != null && !this.configMatcher.matches(command.getConfig())) {
            return false;
        }

        // Check that account matches
        if (this.expectedAccount != null && !this.expectedAccount.equals((command.getAccount()))) {
            return false;
        }

        return true;
    }

    public ExternalCommandMatcher<T> andParam(String param, Object value) {
        if (this.parameterMatcher == null) {
            this.parameterMatcher = new MapContainsMatcher<String, String>();
        }
        this.parameterMatcher.andExpects(param, value.toString());
        return this;
    }
    
    public ExternalCommandMatcher<T> andParams(MapContainsMatcher<String, String> paramMatcher) {
        if (this.parameterMatcher != null) {
            throw new IllegalStateException("Either use a single andParams once or andParam multiple times");
        }
        this.parameterMatcher = paramMatcher;
        return this;
    }
    
    public ExternalCommandMatcher<T> andConfig(String param, Object value) {
        if (this.configMatcher == null) {
            this.configMatcher = new MapContainsMatcher<String, String>();
        }
        this.configMatcher.andExpects(param, value.toString());
        return this;
    }

    public ExternalCommandMatcher<T> andConfig(MapContainsMatcher<String, String> paramMatcher) {
        if (this.configMatcher != null) {
            throw new IllegalStateException("Either use a single andParams once or andParam multiple times");
        }
        this.configMatcher = paramMatcher;
        return this;
    }

    public ExternalCommandMatcher<T> andAccount(SwarmStore expectedAccount) {
        this.expectedAccount = expectedAccount;
        return this;
    }

    public ExternalCommandMatcher<T> andUri(String expectedUri) {
        this.expectedUri = expectedUri;
        return this;
    }
}
