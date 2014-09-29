package com.sonrisa.swarm.kounta.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.kounta.KountaAPI;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;

/**
 * Iteration judge for Kounta, which inspects the HTTP header <code>X-Pages</code>
 * to decide whether this page was the last.
 * 
 * @author Barnabas
 */
public class KountaTerminationJudge implements ExternalIterationJudging {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaTerminationJudge.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean terminated(ExternalResponse lastNode, int pageNumber, int fetchSize) {
        try {
            int pageCount = Integer.parseInt(lastNode.getHeaders().get(KountaAPIReader.PAGE_COUNT_KEY));
            return pageNumber >= pageCount - 1;
        } catch (NumberFormatException e){
            LOGGER.debug("Failed to parse {} to determine last page", KountaAPIReader.PAGE_COUNT_KEY, e);
            
            // Assuming single page
            return true;
        }
    }

}
