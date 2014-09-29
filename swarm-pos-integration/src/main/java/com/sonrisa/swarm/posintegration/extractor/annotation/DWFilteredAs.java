/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.posintegration.extractor.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * This filter shows that a certain entity's time filter
 * in the DataWarehouse is based on which entity.
 * 
 * E.g. InvoiceLineDTO is filtered based on InvoiceDTO 
 * @author sonrisa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DWFilteredAs {

    public Class<? extends DWTransferable> value();
    
}
