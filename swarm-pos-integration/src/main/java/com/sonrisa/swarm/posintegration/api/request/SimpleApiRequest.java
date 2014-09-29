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

import java.util.Iterator;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * Iterable class using the {@link SimpleExternalDTOIterator}
 */
public class SimpleApiRequest<T extends SwarmStore> implements Iterable<ExternalDTO> {

    /**
     * Data reader helps read segmented API result
     */
    private ExternalAPIReader<T> dataReader;

    /**
     * Command used by the {@link ExternalAPI}
     */
    private ExternalCommand<T> command;

    /**
     * Judge judging if the iteration should terminate, because its over
     */
    private ExternalIterationJudging judge;
    
    /**
     * Set first page of iteration
     */
    private int firstPage = 0;
    
    /**
     * Iterable over {@link ExternalDTO} entities.
     * 
     * @param dataReader Data reader to access external data
     * @param command Command for accessing external data
     * @param judge Judge determining when iteration should halt
     */
    public SimpleApiRequest(ExternalAPIReader<T> dataReader, ExternalCommand<T> command, ExternalIterationJudging judge) {
        this.dataReader = dataReader;
        this.command = command;
        this.judge = judge;
    }

    /**
     * Iterable over {@link ExternalDTO} entities with default judge
     * 
     * @param dataReader Data reader to access external data
     * @param command Command for accessing external data
     */
    public SimpleApiRequest(ExternalAPIReader<T> dataReader, ExternalCommand<T> command) {
        this.dataReader = dataReader;
        this.command = command;
        this.judge = new DefaultTerminationJudge(dataReader.getDataKey(command));
    }
    
    @Override
    public Iterator<ExternalDTO> iterator() {
        return new SimpleExternalDTOIterator<T>(dataReader, command, judge, firstPage);
    }
    
    /**
     * Set first page of iteration, some API may require iteration to start from the page 1
     * and not page 0.
     */
    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * Default termination judge judges {@link ExternalDTO} by the 
     * number of data objects, and if its less then the fetch size it forces halting.
     */
    private class DefaultTerminationJudge implements ExternalIterationJudging {
        private ExternalDTOPath path;
        
        public DefaultTerminationJudge(ExternalDTOPath path) {
            this.path = path;
        }

        @Override
        public boolean terminated(ExternalResponse lastNode, int pageNumber, int fetchSize) {
            return lastNode.getContent().getNestedItemSize(path) < fetchSize;
        }
    }
}
