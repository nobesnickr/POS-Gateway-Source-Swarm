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
package com.sonrisa.swarm.posintegration.api.util;

import java.io.IOException;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;

/**
 * The {@link BaseRestAPI} allows exception converters to be set, 
 * in case an {@link IOException} occurs during REST communication.
 * 
 * As the {@link BaseRestAPI} is allowed to throw only {@link ExternalExtractorException},
 * implementations should convert {@link IOException} into {@link ExternalExtractorException}
 */
public interface RestIOExceptionConverter {
    
    /**
     * Convert {@link IOException} to {@link ExternalExtractorException}.
     */
    ExternalExtractorException convertException(IOException occuredException);
}
