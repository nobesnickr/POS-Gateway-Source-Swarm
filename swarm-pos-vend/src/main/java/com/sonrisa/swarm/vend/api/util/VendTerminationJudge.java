package com.sonrisa.swarm.vend.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;

/**
 * Iteration judge for Vend, which inspects the HTTP header <code>X-Pages</code>
 * to decide whether this page was the last.
 */
public class VendTerminationJudge implements ExternalIterationJudging {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VendTerminationJudge.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean terminated(ExternalResponse lastNode, int pageNumber, int fetchSize) {
        try {
            int pageCount = Integer.parseInt(lastNode.getHeaders().get(VendAPIReader.PAGE_COUNT_KEY));
            return pageNumber >= pageCount - 1;
        } catch (NumberFormatException e){
            LOGGER.debug("Failed to parse {} to determine last page", VendAPIReader.PAGE_COUNT_KEY, e);
            
            // Assuming single page
            return true;
        }
    }

}
