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
package com.sonrisa.swarm.revel;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Test class for {@link RevelRequest}
 * 
 * @author sonrisa
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RevelRequestTest {
    
    /** Number of mocked full pages */
    private static final int PAGE_COUNT = 7;
    
    /** Total number of mocked JSON entities */
    final static int TOTAL_COUNT = PAGE_COUNT * RevelAPIReader.PAGE_SIZE + 1;

    /**
     * Mock API
     */
    @Mock
    private ExternalAPI<RevelAccount> mockAPI;
    
    /** Mocked account */
    private RevelAccount mockAccount;
    
    @Before
    public void setUp() throws ExternalExtractorException{
        mockAccount = mock(RevelAccount.class);

        for(int i = 0; i < PAGE_COUNT; i++){
            when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<RevelAccount>("Order")
                    .andParam("limit", RevelAPIReader.PAGE_SIZE)
                    .andParam("offset", i * RevelAPIReader.PAGE_SIZE))))
                    .thenReturn(new ExternalResponse(new ExternalJsonDTO(getMockPage(
                            "objects", 
                            (i-1)*RevelAPIReader.PAGE_SIZE, 
                            RevelAPIReader.PAGE_SIZE,
                            TOTAL_COUNT))));
        }
        
        when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<RevelAccount>("Order")
                .andParam("limit", RevelAPIReader.PAGE_SIZE)
                .andParam("offset", PAGE_COUNT * RevelAPIReader.PAGE_SIZE))))
                .thenReturn(new ExternalResponse(new ExternalJsonDTO(
                        MockDataUtil.toJson(
                                "{\"meta\":{\"total_count\":" + TOTAL_COUNT + "},\"objects\":[{\"key\":\"lastValue\"}]}")
                         )));
        
    }
    /**
     * Test that the request classes iterator is working
     * @throws ExternalExtractorException 
     */
    @Test
    public void testRequestIteration() throws ExternalExtractorException{
        Map<String,String> params = new HashMap<String,String>();
        SimpleApiRequest<RevelAccount> request = new SimpleApiRequest<RevelAccount>(
                new RevelAPIReader(mockAPI), new ExternalCommand<RevelAccount>(mockAccount, "Order"));
        
        Set<String> values = new HashSet<String>();
        for(ExternalDTO item : request){
            values.add(item.getText("key"));
        }
        
        assertEquals(TOTAL_COUNT, values.size());
        verify(mockAPI,times(PAGE_COUNT + 1)).sendRequest(any(ExternalCommand.class));
    }
    
    /** Get mock page */
    private static JsonNode getMockPage(String key, int start, int count, int totalCount){
        StringBuilder content = new StringBuilder();
        for(int i = start; i < start + count; i++){
            if(content.length() != 0){
                content.append(",");
            }
            content.append("{\"key\":\"value" + i + "\"}");
        }
        
        String text = "{\"meta\":{\"total_count\":" + totalCount + "},\"" + key + "\":[" + content.toString() + "]}";
        return MockDataUtil.toJson(text);
    }
}
