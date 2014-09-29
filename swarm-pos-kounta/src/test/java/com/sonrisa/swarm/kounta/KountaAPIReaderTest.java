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
package com.sonrisa.swarm.kounta;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.kounta.api.util.KountaAPIReader;
import com.sonrisa.swarm.kounta.api.util.KountaTerminationJudge;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Unit test for the {@link KountaAPIReader}
 */
@RunWith(MockitoJUnitRunner.class)
public class KountaAPIReaderTest {
    
    /**
     * Mock API
     */
    @Mock
    private ExternalAPI<KountaAccount> api;
    
    /**
     * Total number of pages emulated
     */
    private static final int NUMBER_OF_PAGES = 17;
    
    /**
     * Number of items on a single page
     */
    private static final int ITEMS_PER_PAGE = 25;
    
    /**
     * Target being tested
     */
    private KountaAPIReader target;
    
    /**
     * Setup API reader
     */
    @Before
    public void setupTarget() throws Exception{
        target = new KountaAPIReader(api);
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Create JSON array with empty objects, like [{},{},{},...{},{}]
        final JsonNode mockJson = mapper.readValue("[" + StringUtils.repeat("{},", ITEMS_PER_PAGE-1) + "{}]", JsonNode.class);  
        
        when(api.sendRequest(argThat(new ExternalCommandMatcher<KountaAccount>()))).thenReturn(
                new ExternalResponse(new ExternalJsonDTO(mockJson), new HashMap<String,String>(){{
                    put("X-Pages", Integer.toString(NUMBER_OF_PAGES));
                }})
        );
    }
    
    /**
     * Test case:
     *  When sending a request to the KOUNTA service,
     *  it returns with X-Pages: 17
     *  
     * Expected:
     *  Pages 0,1,2..16 are fetched and no other
     */
    @Test
    public void testAllPageIsFetched() throws Exception {

        KountaAccount account = new KountaAccount(8L);
        
        Iterable<ExternalDTO> items = new SimpleApiRequest<KountaAccount>(
                                            target, 
                                            new ExternalCommand<KountaAccount>(account, "some/where.json"),
                                            new KountaTerminationJudge());
        
        // Iterate through all of them        
        int numberOfItems = 0;
        for(ExternalDTO item : items){
            ++numberOfItems;
        }
        
        assertEquals(ITEMS_PER_PAGE * NUMBER_OF_PAGES, numberOfItems);
        
        verify(api, times(NUMBER_OF_PAGES)).sendRequest(any(ExternalCommand.class));
        verify(api, times(1)).sendRequest(argThat(new ExternalCommandMatcher<KountaAccount>().andConfig("X-Page","0")));
        verify(api, never()).sendRequest(argThat(new ExternalCommandMatcher<KountaAccount>().andConfig("X-Page",Integer.toString(NUMBER_OF_PAGES))));
    }
}
