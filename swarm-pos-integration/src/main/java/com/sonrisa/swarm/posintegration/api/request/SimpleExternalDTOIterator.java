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

package com.sonrisa.swarm.posintegration.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalDTOIterator;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Iterator over an external data
 * @param <T>
 */
public class SimpleExternalDTOIterator<T extends SwarmStore> implements ExternalDTOIterator<SwarmStore> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExternalDTOIterator.class);
	
    /**
     * Data reader helps read segmented API result
     */
    protected ExternalAPIReader<T> dataReader;

    /**
     * Command used by the {@link ExternalAPI}
     */
    protected ExternalCommand<T> command;

    /**
     * Judge judging if the iteration should terminate, because its over
     */
    private ExternalIterationJudging judge;

    /**
     * Results are fetched in pages, each with e.g. 100 records, this shows the
     * 0..99 index within the page
     */
    protected int indexWithinPage = 0;
    /**
     * If records exceed the limit of fetchSize, they can be requested with
     * specifying a page index
     */
    private int pageNumber = 0;

    /**
     * Current page's records, the cached result of
     * {@link ExternalAPIReader#getPage(int)}
     */
    protected ExternalResponse currentPage = null;

    /**
     * Iterator over {@link ExternalDTO} entities.
     * 
     * @param dataReader Data reader to access external data
     * @param command Command for accessing external data
     * @param judge Judge determining when iteration should halt
     */
    public SimpleExternalDTOIterator(ExternalAPIReader<T> dataReader, ExternalCommand<T> command, ExternalIterationJudging judge) {
        this.dataReader = dataReader;
        this.command = command;
        this.judge = judge;
    }
    
    /**
     * Iterator over {@link ExternalDTO} entities.
     * 
     * @param dataReader Data reader to access external data
     * @param command Command for accessing external data
     * @param judge Judge determining when iteration should halt
     * @param firstPage First page of iteration in case this might be 1 or something else
     */
    public SimpleExternalDTOIterator(ExternalAPIReader<T> dataReader, ExternalCommand<T> command, ExternalIterationJudging judge, int firstPage) {
        this(dataReader, command, judge);
        this.pageNumber = firstPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        try {
        	
            // If the first page was never fetched, fetch it now
            if (currentPage == null) {
                currentPage = dataReader.getPage(command, pageNumber);
                indexWithinPage = 0;
                LOGGER.debug(" Fetching first page");
            }
            final ExternalDTO currentContent = currentPage.getContent();
            // If there are items on the current page, then
            // there are more items to iterate over globally
            if (indexWithinPage < currentContent.getNestedItemSize(dataReader.getDataKey(command))) {
                return true;
                // If we've reached the last item on the page
            } else {
                // Use failsafe mechanism to always return false from
                // empty result sets page as if it's last
                if (currentContent.getNestedItemSize(dataReader.getDataKey(command)) == 0) {
                    return false;

                    // Or if this was the last page, it's time to terminate,
                } else if (judge.terminated(currentPage, pageNumber, dataReader.getFetchSize())) {
                    return false;

                    // Otherwise fetch next page, and recheck
                    // if we've reached the last page
                    //
                    // Note that even if this isn't the last page, the next
                    // one might be the last with zero elements and there
                    // are in fact no more items
                } else {
                    // Fetch next page
                    currentPage = dataReader.getPage(command, ++pageNumber);
                    indexWithinPage = 0;
                    return hasNext();
                }
            }
        } catch (ExternalExtractorException e) {
            throw new ExternalPageIterationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTO next() {
        // Fetching new page is done in hasNext when
        // reaching the end of each page
        if (hasNext()) {
            try {
            	return currentPage.getContent()
                        .getNestedItem(dataReader.getDataKey(command))
                        .getNestedArrayItem(indexWithinPage++);
            } catch (ExternalExtractorException e) {
                throw new ExternalPageIterationException(e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing from external data source is not supported.");
    }
}
