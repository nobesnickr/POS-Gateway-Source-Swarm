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

package com.sonrisa.swarm.posintegration.api.reader;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Data source of {@link ExternalDTO} entities which allows reading it on page at a time
 */
public interface ExternalAPIReader<T extends SwarmStore> {

    /**
     * Gets a dedicated page from large result set
     * 
     * @param api API to execute command
     * @param command Command to be executed
     * @param page Page to be fetched
     * @return JsonNode and headers of the page
     */
    ExternalResponse getPage(ExternalCommand<T> command, int page) throws ExternalExtractorException;
    
    /**
     * Get dataKey for a command. We assume that the reader's result contains meta information (number of elements, status),
     * and data, using the {@link ExternalDTO#getNestedItem(String)} gives access to the data.
     * 
     * @param command Data key may be different for each command
     * @return The dataKey using which data can be accessed
     */
    ExternalDTOPath getDataKey(ExternalCommand<T> command);
    
    /**
     * Gets the fetch size of dataReader, i.e. the maximum number of elements on a page.
     * We assume that if the page contains less elements than the fetch size, that's because
     * there are no more elements in to whole result set.
     * 
     * @return Page size for the reader
     */
    int getFetchSize();
    
}
