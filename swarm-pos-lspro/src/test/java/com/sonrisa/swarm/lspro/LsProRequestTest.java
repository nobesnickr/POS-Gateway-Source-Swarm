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

package com.sonrisa.swarm.lspro;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.lspro.MockLsProData;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Unit tests for the {@link LsProRequest} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class LsProRequestTest {

    /**
     * Mock API
     */
    @Mock
    private ExternalAPI<LsProAccount> mockAPI;

    /**
     * Account used to execute
     */
    @Mock
    private LsProAccount account;

    /**
     * Page set in the mocks, and page size set for the request
     */
    private static final int PAGE_SIZE = 50;

    /**
     * Setting up context for test by initializing mock (re)initializing mock
     * instances before each test;
     * 
     * @throws ExternalExtractorException
     */
    @Before
    public void setupContext() throws ExternalExtractorException {

        // Matching any query to invoices containing skip=0
        when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$skip", 0))))
                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_INVOICES_PAGE_1));

        // Matching any query to invoices containing skip=0
        when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<LsProAccount>("Invoices").andParam("$skip", 50))))
                .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockLsProData.MOCK_LSPRO_INVOICES_PAGE_2));

    }

    /**
     * Test case: - There are more records than they could fit on a page (
     * {@link LsProRequest#ITEMS_PER_PAGE})
     */
    @Test
    public void testBaseCaseScenario() throws ExternalExtractorException {

        LsProAPIReader apiReader = new LsProAPIReader(mockAPI);
        apiReader.setFetchSize(PAGE_SIZE); // Forcing page size to, the size of the mock
        
        // Act
        ExternalCommand<LsProAccount> invoiceCommand = new ExternalCommand<LsProAccount>(account, "Invoices");
        Iterable<ExternalDTO> request = new SimpleApiRequest<LsProAccount>(apiReader, invoiceCommand);

        // Assert number of items
        assertEquals("Number of invoices in request doesn't match the jsons",
                     // Expected number of items in mocks
                     MockLsProData.getLsProMockDescriptor().getCountForDTOClass(InvoiceDTO.class),
                     // Actual number of items in request
                     Lists.newArrayList(request).size()     
        );
        
        // Assert number of pages fetched
        verify(mockAPI,times(2)).sendRequest(any(ExternalCommand.class));
    }
}
