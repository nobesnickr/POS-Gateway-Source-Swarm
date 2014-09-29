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
package com.sonrisa.swarm;

import com.sonrisa.swarm.common.util.DateUtil;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;

/**
 * Class providing Mock objects specific to Retail Pro
 * @author sonrisa
 *
 */
public class MockRetailProData {

    /**
     * Creates a mock JSON store object.
     * 
     * @param name
     * @param sbs
     * @param stNo
     * @return 
     */
    public static JsonStore mockRpJsonStore(final String name, final String sbs, final String stNo){
        final JsonStore jsonStore = new JsonStore();
        jsonStore.setName(name);
        jsonStore.setSbsNumber(sbs);
        jsonStore.setStoreNumber(stNo);
        return jsonStore;
    }

    /**
     * Creates a mock {@link RpStoreEntity} object.
     * 
     * @param swarmId
     * @param sbsNo
     * @param storeNo
     * @return 
     */
    public static RpStoreEntity mockRpStoreEntity(final String swarmId, final String sbsNo, final String storeNo) {
        RpStoreEntity rpStore = new RpStoreEntity();
        rpStore.setSbsNumber(sbsNo);
        rpStore.setStoreNumber(storeNo);
        rpStore.setSwarmId(swarmId);
        rpStore.setTimeZone(DateUtil.getDefaultTimeZoneCode());
        return rpStore;
    }
}
