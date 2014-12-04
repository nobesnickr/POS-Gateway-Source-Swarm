package com.sonrisa.swarm.vend;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;
import com.sonrisa.swarm.vend.api.util.VendAPIReader;
import com.sonrisa.swarm.vend.api.util.VendTerminationJudge;

/**
 * Unit test for the {@link VendAPIReader}
 */
@RunWith(MockitoJUnitRunner.class)
public class VendAPIReaderTest {
    
    /**
     * Mock API
     */
    @Mock
    private ExternalAPI<VendAccount> api;
    
    /**
     * Total number of pages emulated
     */
    private static final int NUMBER_OF_PAGES = 17;
    
    /**
     * Number of items on a single page
     */
    private static final int ITEMS_PER_PAGE = 50;
    
    /**
     * Target being tested
     */
    private VendAPIReader target;
    
    /**
     * Setup API reader
     */
    @Before
    public void setupTarget() throws Exception{
        target = new VendAPIReader(api);
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Create JSON array with empty objects, like [{},{},{},...{},{}]
        final JsonNode mockJson = mapper.readValue("[" + StringUtils.repeat("{},", ITEMS_PER_PAGE-1) + "{}]", JsonNode.class);  
        
        when(api.sendRequest(argThat(new ExternalCommandMatcher<VendAccount>()))).thenReturn(
                new ExternalResponse(new ExternalJsonDTO(mockJson), new HashMap<String,String>(){{
                    put("pages", Integer.toString(NUMBER_OF_PAGES));
                }})
        );
    }
    
    /**
     * Test case:
     *  When sending a request to the VEND service,
     *  it returns with pages: 17
     *  
     * Expected:
     *  Pages 0,1,2..16 are fetched and no other
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testAllPageIsFetched() throws Exception {

        VendAccount account = new VendAccount(8L);
        
        Iterable<ExternalDTO> items = new SimpleApiRequest<VendAccount>(
                                            target, 
                                            new ExternalCommand<VendAccount>(account, "some/where.json"),
                                            new VendTerminationJudge());
        
        // Iterate through all of them        
        int numberOfItems = 0;
        for(ExternalDTO item : items){
            ++numberOfItems;
        }
        
        // Do we recover all the items?
        assertEquals(ITEMS_PER_PAGE * NUMBER_OF_PAGES, numberOfItems);
        // Do we call the rest API all the times that are needed? (not more not less)
        verify(api, times(NUMBER_OF_PAGES)).sendRequest(any(ExternalCommand.class));
        // Do we call the rest API only one time for each page?
        verify(api, times(1)).sendRequest(argThat(new ExternalCommandMatcher<VendAccount>().andParam("page","0")));
        // For an incorrect input do we call the API unnecessarily?
        verify(api, never()).sendRequest(argThat(new ExternalCommandMatcher<VendAccount>().andParam("page",Integer.toString(NUMBER_OF_PAGES))));
    }
}
