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
package com.sonrisa.swarm.mos;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.mos.MockMosData;
import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.test.matcher.ListSizeMatcher;

/**
 * Class testing the integrity of the Merchant OS 
 * extraction plugin, by using real JSON files for
 * mocking the behaviour of the remote system.
 */
public class MosExtractorTest {
    
    /** Mock token used for authentication */
    private static final String oauthToken = "abc1a1cc351f3aa31ec2921951e1acee3073b4789770ea6a374432d150fbdc73";
    
    /** Tested class */
    private MosExtractor extractor;
    
    /** Account used during mocking */
    private MosAccount account;
    
    /**
     * Mocked API
     */
    private MosAPI api;
    
    /**
     * Mocked DataStore
     */
    private SwarmDataWarehouse dataStore;
    
    /**
     * Filter date of the dataStore
     */
    private Date mosDate = new Date();
    
    /**
     * Initial setup of the mock service
     * @throws ExternalExtractorException 
     */
    @Before
    public void setUp() throws ExternalExtractorException{
        
        this.api = mock(MosAPI.class);
        
        when(api.sendRequest(any(MosAccount.class), startsWith("Category?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_CATEGORIES));

        when(api.sendRequest(any(MosAccount.class), startsWith("Item?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_PRODUCTS));

        when(api.sendRequest(any(MosAccount.class), startsWith("Customer?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_CUSTOMERS));

        when(api.sendRequest(any(MosAccount.class), startsWith("Manufacturer?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_MANUFACTURER));

        when(api.sendRequest(any(MosAccount.class), startsWith("SaleLine?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_SALE_LINE));

        when(api.sendRequest(any(MosAccount.class), startsWith("Sale?")))
        .thenReturn(MockDataUtil.getResourceAsJson(MockMosData.MOCK_MOS_SALE));
        
        // Setup test context
        this.extractor = new MosExtractor(api);
        this.extractor.setDtoTransformer(new ExternalDTOTransformer());
        
        // Setup account
        account = new MosAccount(3L);
        account.setUrlBase(new String("http://localhost:5555/API/").getBytes());
        account.setOauthToken(oauthToken.getBytes());
        account.setAccountId("63593");
        
        dataStore = mock(SwarmDataWarehouse.class);
        when(dataStore.getFilter(any(SwarmStore.class), any(Class.class)))
            .thenReturn(DWFilter.fromTimestamp(new Timestamp(mosDate.getTime())));
    }
    
    
    /**
     * Test that the number of extracted DTO items
     * matched the number in the mock JSON files
     * @throws ExternalExtractorException
     */
    @Test
    public void testQuantityOfItemsExtracted() throws ExternalExtractorException {
        
        extractor.fetchData(account, dataStore);
        
        assertDTOSaved(dataStore, CategoryDTO.class);
        assertDTOSaved(dataStore, ManufacturerDTO.class);
        assertDTOSaved(dataStore, ProductDTO.class);
        assertDTOSaved(dataStore, CustomerDTO.class);
        assertDTOSaved(dataStore, InvoiceDTO.class);
        assertDTOSaved(dataStore, InvoiceLineDTO.class);
    }
    
    /**
     * Test that timestamp URL parameter is properly added
     * @throws ExternalExtractorException
     */
    @Test
    public void testTimestampAndShopFilterSent() throws ExternalExtractorException {
        
        final String shopId = "1234"; 
        account.setShopId(shopId);
        extractor.fetchData(account, dataStore);

        // TODO when refactoring with ExternalCommand don't encode URI here
        String timeStamp = ISO8061DateTimeConverter.dateToMerchantOSURIEncodedString(mosDate);
        
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(api, Mockito.times(6)).sendRequest(any(MosAccount.class), captor.capture());
        
        for(int i = 0; i < 6; i++){
            final String value = captor.getAllValues().get(i);
            assertTrue(value + " contains invalid timestamp, not " + timeStamp, 
                        value.contains("timeStamp=%3E," + timeStamp));
            
            // Sale and SaleLine starts with "Sale", these requests should contain the shopId
            if(value.startsWith("Sale")){
                assertTrue(value + " doesn't contain shopId filter " + shopId, 
                        value.contains("shopID=IN,[" + shopId + "]"));
            }
        }
    }
    
    /**
     * Asserts that a DTO was saved into the datastore a certain number of times
     * @param dataStore
     * @param clazz
     * @param expected
     */
    private static <T extends DWTransferable> void assertDTOSaved(SwarmDataWarehouse dataStore, Class<T> clazz){
        Map<String, Integer> correctCount = MockMosData.getCountOfMockJsonItems();
        verify(dataStore).save(
                any(SwarmStore.class), 
                argThat(new ListSizeMatcher<T>(correctCount.get(clazz.getSimpleName()))), 
                eq(clazz));
    }
}
