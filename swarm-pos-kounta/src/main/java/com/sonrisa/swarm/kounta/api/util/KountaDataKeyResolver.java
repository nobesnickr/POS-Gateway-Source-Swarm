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

package com.sonrisa.swarm.kounta.api.util;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.posintegration.api.reader.impl.SimpleDataKeyResolver;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * Data key resolver for Kounta.
 * 
 * As there is no data key for Kounta this class returns a path to the data
 * to be the top level.
 */
public class KountaDataKeyResolver extends SimpleDataKeyResolver<KountaAccount>{

    /**
     * Initialize for root level data key
     */
    public KountaDataKeyResolver() {
        super(ExternalDTOPath.getRootPath());
    }
}
