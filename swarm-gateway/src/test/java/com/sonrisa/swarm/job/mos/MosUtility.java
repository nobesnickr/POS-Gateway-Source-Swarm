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
package com.sonrisa.swarm.job.mos;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.mos.MockMosData;

/**
 * Class containing various utility tools
 * helping other Unit tests focused on the Merchant OS
 * extractor
 */
public class MosUtility {
    
    /**
     * Initial setup of the mock service
     */
    public static void setUpWiremock(){
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/Category\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_CATEGORIES))));
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/Item\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_PRODUCTS))));

        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/Customer\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_CUSTOMERS))));
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/Manufacturer\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_MANUFACTURER))));
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/Sale\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_SALE))));
        
        stubFor(WireMock.get(urlMatching("/API/Account/[0-9]+/SaleLine\\?.*"))
                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockMosData.MOCK_MOS_SALE_LINE))));
    }
}
