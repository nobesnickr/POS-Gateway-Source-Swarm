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
package com.sonrisa.swarm.posintegration.warehouse;

import com.sonrisa.swarm.model.StageBatchInsertable;
import java.sql.Timestamp;


/**
 * A data transfer object is a superclass of objects that are saved into the datastore,
 * normally they have an id inherited from the foreign system and a version(or timeStamp)
 * property that helps the extractor to only fetch recently changed data objects from
 * the remote system.
 */
public interface DWTransferable extends StageBatchInsertable {
    
    /** Key inherited from the remote system */
    public long getRemoteId();
    	
	/** Get timestamp of the object */
	public Timestamp getLastModified();
}
