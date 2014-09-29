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

import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;

/** 
 * Implementing interfaces are capable of judging whether the 
 * iteration over an {@link ExternalAPIReader} should halt.
 */
public interface ExternalIterationJudging {

    /**
     * Judge JSON and decide if current page is the last page
     * 
     * @param lastNode JsonNode and headers over which we're currently iterating over
     * @param pageNumber Current page
     * @param fetchSize Fetch size with which pages were fetched
     * @return <i>true</i> if iteration should be terminated, <i>false</i> not sure
     */
    boolean terminated(ExternalResponse lastNode, int pageNumber, int fetchSize);
}
