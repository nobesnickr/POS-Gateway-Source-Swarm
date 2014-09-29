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
package com.sonrisa.swarm.shopify;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sonrisa.shopify.ShopifyAPIReader;
import com.sonrisa.shopify.ShopifyAccount;
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
 * Test class for {@link ShopifyRequest}
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ShopifyRequestTest {

    /**
     * Mock API
     */
    @Mock
    private ExternalAPI<ShopifyAccount> mockAPI;
    
    private static final int PAGE_COUNT = 7;
    
    @Before
    public void setUp() throws ExternalExtractorException{
        
        for(int i = 1; i <= PAGE_COUNT; i++){
            when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<ShopifyAccount>("products.json")
                    .andParam("limit", 250)
                    .andParam("page", i))))
                    .thenReturn(getMockPage("products", (i-1)*ShopifyAPIReader.PAGE_SIZE, ShopifyAPIReader.PAGE_SIZE));
        }
        
        when(mockAPI.sendRequest(argThat(new ExternalCommandMatcher<ShopifyAccount>("products.json")
                .andParam("limit", 250)
                .andParam("page", PAGE_COUNT+1))))
                .thenReturn(new ExternalResponse(new ExternalJsonDTO(MockDataUtil.toJson("{\"products\":[]}"))));
    }
    
    /**
     * Test iteration over pages
     * @throws ExternalExtractorException 
     */
    @Test
    public void testPageIterationAccess() throws ExternalExtractorException{
        
        ShopifyAccount account = new ShopifyAccount();

        SimpleApiRequest<ShopifyAccount> request = new SimpleApiRequest<ShopifyAccount>(
                new ShopifyAPIReader(mockAPI), new ExternalCommand<ShopifyAccount>(account, "products.json"));
        
        // First page is 1 (and not 0)
        request.setFirstPage(1);
        
        Set<String> values = new HashSet<String>();
        for(ExternalDTO item : request){
            values.add(item.getText("key"));
        }
        
        assertEquals(PAGE_COUNT * ShopifyAPIReader.PAGE_SIZE, values.size());
        verify(mockAPI,times(PAGE_COUNT + 1)).sendRequest(any(ExternalCommand.class));
    }
    
    /** 
     * Get mock page 
     */
    private static ExternalResponse getMockPage(String key, int start, int count){
        StringBuilder content = new StringBuilder();
        for(int i = start; i < start + count; i++){
            if(content.length() != 0){
                content.append(",");
            }
            content.append("{\"key\":\"value" + i + "\"}");
        }
        
        String text = "{\"" + key + "\":[" + content.toString() + "]}";
        
        return new ExternalResponse(new ExternalJsonDTO(MockDataUtil.toJson(text))); 
    }
}
