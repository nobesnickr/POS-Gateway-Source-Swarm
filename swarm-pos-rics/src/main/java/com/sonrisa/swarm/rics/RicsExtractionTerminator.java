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
package com.sonrisa.swarm.rics;

import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * RICS uses {@link RicsApiReader} to extract long batch data from RICS.
 * This process should be terminated on some conditions defined in this class
 * @author Sonrisa
 *
 */
public class RicsExtractionTerminator implements ExternalIterationJudging {

	/**
	 * extraction should be terminated when 'EndRecord' value reaches 'TotalRecords' in the response JSON's 'ResultStatistics' node.
	 */
	@Override
	public boolean terminated(ExternalResponse page, int pageNumber, int fetchSize) {
		try {
			ExternalDTO resultStatistics = page.getContent().getNestedItem(new ExternalDTOPath("ResultStatistics"));
			int endRecord = resultStatistics.getInt("EndRecord");
			int totalRecords = resultStatistics.getInt("TotalRecords");

			// if EndRecord is zero or smaller than TotalRecord, we have more data and we should fetch it from the remote service
			if (totalRecords == 0 || endRecord >= totalRecords) {
				return true;
			}
			
			return false;
		} catch (ExternalExtractorException e) {
			throw new ExternalPageIterationException(e);
		}
	}
}
