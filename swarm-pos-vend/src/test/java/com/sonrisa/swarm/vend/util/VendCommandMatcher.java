package com.sonrisa.swarm.vend.util;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.vend.VendAccount;

public class VendCommandMatcher extends ExternalCommandMatcher<VendAccount>{

	public VendCommandMatcher(String expectedUri) {
        super(expectedUri);
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

        // Check that account matches
        if (this.expectedAccount != null && !this.expectedAccount.equals((command.getAccount()))) {
            return false;
        }

        return true;
    }
	
}
