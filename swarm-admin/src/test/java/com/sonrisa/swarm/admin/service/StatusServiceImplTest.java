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
package com.sonrisa.swarm.admin.service;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.OrderDirection;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.model.query.StatusQueryEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.admin.service.impl.StatusProcessingServiceImpl;
import com.sonrisa.swarm.admin.service.impl.StatusServiceImpl;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;
import com.sonrisa.swarm.posintegration.service.impl.ApiServiceImpl;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;

/**
 * Class testing the {@link StatusServiceImpl} class.
 */
public class StatusServiceImplTest extends BaseStatusServiceTest {

	/**
	 * Target to be tested
	 */
	private StatusServiceImpl target;
	
	/**
	 * Status service
	 */
	private StatusProcessingServiceImpl statusService;
	
	/**
	 * Mock service containing "shopify" and "php-api"
	 */
	private ApiServiceImpl apiService;
	
	/**
	 * API id for "shopify" which is gateway managed
	 */
	private static final Long API_ID = 5L;
	
	/**
	 * API id for "php-api" which isn't gateway managed
	 */
	private static final Long OTHER_API_ID = 6L;
	
	/**
	 * Setup tests
	 */
	@Before
	public void setUp(){
	    // Setup apiService
        apiService = new ApiServiceImpl();
        apiService.fillCache(Arrays.asList(
                new ApiEntity(API_ID, "shopify", ApiType.PULL_API),
                new ApiEntity(OTHER_API_ID, "php-api", ApiType.SWARM_API)
        ));
        
        // Setup extractor monitoring service
        ExtractorMonitoringService extractorMonitor = mock(ExtractorMonitoringService.class);
        when(extractorMonitor.getLastSuccessfulExecution(anyLong())).thenReturn(new Date());
        
        
        statusService = new StatusProcessingServiceImpl();
		statusService.setApiService(apiService);
		statusService.setExtractorMonitoringService(extractorMonitor);
		statusService.setInvoiceCountService(mockInvoiceCountMonitoringService());
		
		StoreDao storeDao = mock(StoreDao.class);

        target = new StatusServiceImpl(statusService, storeDao, apiService);
        target.setGlobalQueryLimit(1000);
	}
	
	/**
	 * Test case: There is a store in the database
	 * 
	 * Expected: Its returned by the service and its top-level fields are as expected
	 */
	@Test
	public void testCommonFields() throws InvalidStatusRequestException{
	    
	    final StoreEntity store = mockStore(1L, API_ID);
	    final StoreEntity inactiveStore = mockStore(2L, API_ID);
	    inactiveStore.setActive(Boolean.FALSE);
	    
		// StoreDao returns this
		StoreDao mockDao = mock(StoreDao.class);
		when(mockDao.find(any(JpaFilter.class),anyLong(),anyLong())).thenReturn(
			Arrays.asList(store, inactiveStore)				
		);
		
		target.setSourceDao(mockDao);
		
		// Set query configuration
		StatusQueryEntity config = new StatusQueryEntity();
		
		// Act
		List<StatusEntity> retVal = target.getStoreStatuses(config);
		
		// Assert
		SimpleDateFormat dateFormat = new SimpleDateFormat(StatusProcessingService.STORE_STATUS_DATE_FORMAT);
		
        assertEquals(2, retVal.size());
        assertEquals(store.getActive().toString(), retVal.get(0).getActive());
        assertEquals(dateFormat.format(store.getCreated()), retVal.get(0).getCreated());
        assertEquals(store.getName(), retVal.get(0).getName());
        assertEquals(store.getNotes(), retVal.get(0).getNotes());
        assertEquals(apiService.findById(API_ID).getApiName(), retVal.get(0).getApi());
        
        assertEquals(inactiveStore.getActive().toString(), retVal.get(1).getActive());
        assertEquals(dateFormat.format(inactiveStore.getCreated()), retVal.get(1).getCreated());
        assertEquals(inactiveStore.getName(), retVal.get(1).getName());
        assertEquals(inactiveStore.getNotes(), retVal.get(1).getNotes());
        assertEquals(apiService.findById(API_ID).getApiName(), retVal.get(1).getApi());
	}
	
	/**
     * Test case:
     *  Executing {@link StatusServiceImpl#processStores(List, int, int)} with skip > 0 and take < number of entities - skip 
     *  
     * Expected:
     *  Only the subset specified by skip and take is returned
     */
    @Test
    public void testSkipAndTake() throws InvalidStatusRequestException{
        
        int skip = 5;
        int take = 7;

        // Set Dao to return 20 stores
        target.setSourceDao(mockStoreDao(20, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setSkip(skip);
        config.setTake(take);
        
        // Act
        List<StatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert        
        assertEquals(take, retVal.size());
        for(int i = 0; i < take; i++){
            // Mocked stores have ids 0,1,2...20, verify that 5,6...11 was selected 
            assertEquals(new Long(skip + i), retVal.get(i).getStoreId());
        }
    }
	
	/**
	 * Test case:
	 *  Executing {@link StatusServiceImpl#processStores(List, int, int)} with very large take. 
	 *  
	 * Expected:
	 *  When executing with take
	 *  exceeding the {@link StatusServiceImpl#setGlobalQueryLimit(Integer)}'s value, then
	 *  the global query configuration overrules to take specified.
	 */
	@Test
	public void testTakeHasGlobalLimit() throws InvalidStatusRequestException{

	    int globalLimit = 2;
	    
        // Set global limit to 2 < 3
        target.setGlobalQueryLimit(globalLimit);
        
        // Set Dao to return 3 stores
        target.setSourceDao(mockStoreDao(3, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setTake(200);
        
        // Act
        List<StatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert        
        assertEquals(globalLimit, retVal.size());
	}
	
	/**
	 * Test case:
	 *  Sending request with a negative <strong>skip</strong>
	 *  
	 * Expected:
	 *  Exception is thrown
	 *  
	 * @throws InvalidStatusRequestException 
	 */
	@Test(expected = InvalidStatusRequestException.class)
	public void testNegativeSkip() throws InvalidStatusRequestException{

        // Set Dao to return 3 stores
        target.setSourceDao(mockStoreDao(3, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setSkip(-7);
        
        // Act
        target.getStoreStatuses(config);
	}

    /**
     * Test case:
     *  Sending request with a negative <strong>take</strong>
     *  
     * Expected:
     *  Exception is thrown
     *  
     * @throws InvalidStatusRequestException 
     */
    @Test(expected = InvalidStatusRequestException.class)
	public void testNegativeTake() throws InvalidStatusRequestException{

        // Set Dao to return 3 stores
        target.setSourceDao(mockStoreDao(3, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setTake(-8);
        
        // Act
        target.getStoreStatuses(config);
	}
		
    /**
     * Test case:
     *  Sending request with an order which is not a column of 
     *  {@link StoreEntity}
     *  
     * @throws InvalidStatusRequestException 
     */
    @Test(expected = InvalidStatusRequestException.class)
	public void testIllegalOrder() throws InvalidStatusRequestException{

        // Set Dao to return 3 stores
        target.setSourceDao(mockStoreDao(3, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setOrderBy("unreal_column");
        
        // Act
        target.getStoreStatuses(config);
	}
	
    /**
     * Test case:
     *  Sending request with an order which is a column of  
     *  {@link StoreEntity}, but a byte[] array, like <code>api_key</code>
     *  or <code>api_url</code> 
     *  
     * @throws InvalidStatusRequestException 
     */
    @Test(expected = InvalidStatusRequestException.class)
	public void testOrderByByteArray() throws InvalidStatusRequestException{

        // Set Dao to return 3 stores
        target.setSourceDao(mockStoreDao(3, API_ID));
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setOrderBy("api_key");
        
        // Act
        target.getStoreStatuses(config);
	}
	
    /**
     * Test case:
     *  Requesting status for stores without specifying which apis
     * 
     * Expected:
     *  Only apis associcated with the gateway (e.g shopify but not vend)
     *  are returned, because a where clause is added to the sql query.
     *  
     */
    @Test
	public void testOnlyGatewayApisAreReturned() throws InvalidStatusRequestException{
        // Set Dao to return 3 stores, 2 with gateway related api, 1 other
        StoreDao mockDao = mockStoreDao(Arrays.asList(mockStore(1L, API_ID), mockStore(2L, OTHER_API_ID), mockStore(3L, API_ID)));
        target.setSourceDao(mockDao);
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
                
        // Act
        target.getStoreStatuses(config);
        
        // Verify that WHERE api_id = POS_API was added to query
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("apiId", Arrays.asList(API_ID), "IN", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
	}
    
    /**
     * Test case:
     *  Requesting status for stores with specifying a gateway apis
     * 
     * Expected:
     *  Where clause for active stores is added for these apis
     *  
     */
    @Test
    public void testFilterByGatewayApi() throws InvalidStatusRequestException{
        
        final Long apiId = 20L;
        final Long apiId2 = 30L;
        
        ApiServiceImpl mockApiService = new ApiServiceImpl();
        mockApiService.fillCache(Arrays.asList(
                new ApiEntity(apiId, "shopify", ApiType.PULL_API),
                new ApiEntity(apiId2, "revel", ApiType.PULL_API),
                new ApiEntity(OTHER_API_ID, "ruby-api", ApiType.SWARM_API)
        ));
                
        statusService.setApiService(mockApiService);
        target.setApiService(mockApiService);
        
        // Set Dao to return 3 stores, 2 with gateway related api, 1 other
        StoreDao mockDao = mockStoreDao(Arrays.asList(mockStore(1L, apiId), mockStore(2L, OTHER_API_ID), mockStore(3L, apiId2)));
        target.setSourceDao(mockDao);
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setApi(new HashSet<String>(Arrays.asList(
                mockApiService.findById(apiId).getApiName(), mockApiService.findById(apiId2).getApiName()
       )));
                
        // Act
        target.getStoreStatuses(config);
        
        // Verify that WHERE api_id = POS_API was added to query
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("apiId", Arrays.asList(apiId, apiId2), "IN", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }

    /**
     * Test case:
     *  Requesting status for active stores
     * 
     * Expected:
     *  Where clause for active stores is added to the query.
     */
    @Test
    public void testFilterByActive() throws InvalidStatusRequestException{
       
        // Set Dao to return 5
        StoreDao mockDao = mockStoreDao(5, API_ID);
        target.setSourceDao(mockDao);
        
        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setActive(Boolean.TRUE);
                
        // Act
        target.getStoreStatuses(config);
        
        // Verify that WHERE api_id = POS_API was added to query
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("active", config.getActive(), "=", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }
    
    /**
     * Test case:
     *  One of the rows in the <code>stores</code> table
     *  has <i>null</i> in it's active column
     *  
     * Expected:
     *  {@link StatusProcessingServiceImpl} doesn't fail with {@link NullPointerException}
     */
    @Test
    public void testNullValueForActive() throws InvalidStatusRequestException{
     
        StoreEntity store = mockStore(1L, API_ID);
        store.setActive(null);
        
        // Set this single store
        target.setSourceDao(mockStoreDao(Arrays.asList(store)));

        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
                
        // Act
        target.getStoreStatuses(config);
    }
    
    /**
     * Test case: 
     *  We have three stores in the database, an empty, an old and a normal.
     *  
     * Expected:
     *  The store statu's detail segment contains information on the
     *  last successful extraction and the number of invoices in the DB,
     *  which are read from the monitoring services.
     *  
     */
    @Test
    public void testStatusDetails() throws InvalidStatusRequestException {

        final Long emptyStore = 1L;
        final Long outdatedStore = 2L;
        final Long uptoDateStore = 3L;
        final Long inactiveStore = 4L;
        
        final Date outdatedDate = new Date(1000000000L * 1000L); // Sun, 09 Sep 2001 01:46:40 GMT
        final Date date = new Date();
        final long normalInvoiceCount = 500;
        
        // Set Dao to return the stores
        StoreEntity inactiveStoreEntity = mockStore(inactiveStore, API_ID);
        inactiveStoreEntity.setActive(Boolean.FALSE);
        
        StoreDao mockDao = mockStoreDao(Arrays.asList(
                mockStore(emptyStore, API_ID), mockStore(outdatedStore, API_ID), mockStore(uptoDateStore, API_ID), inactiveStoreEntity));
        target.setSourceDao(mockDao);
        
        // Setup extractor monitoring service
        ExtractorMonitoringService extractorMonitor = mock(ExtractorMonitoringService.class);
        when(extractorMonitor.getLastSuccessfulExecution(eq(outdatedStore))).thenReturn(outdatedDate);
        when(extractorMonitor.getLastSuccessfulExecution(not(eq(outdatedStore)))).thenReturn(date);
        statusService.setExtractorMonitoringService(extractorMonitor);
        
        // Setup invoice monitoring service
        InvoiceCountMonitoringService invoiceMonitor = mock(InvoiceCountMonitoringService.class);
        when(invoiceMonitor.getInvoiceCount(eq(emptyStore))).thenReturn(0L);
        when(invoiceMonitor.getLastInvoiceDate(eq(emptyStore))).thenReturn(new Date(0L));
        when(invoiceMonitor.getInvoiceCount(not(eq(emptyStore)))).thenReturn(normalInvoiceCount);
        when(invoiceMonitor.getLastInvoiceDate(not(eq(emptyStore)))).thenReturn(date);
        statusService.setInvoiceCountService(invoiceMonitor);

        // Act
        StatusQueryEntity config = new StatusQueryEntity();
        List<StatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert
        assertEquals(4, retVal.size());
        for(StatusEntity statusRow : retVal){
            if(statusRow.getStoreId().equals(emptyStore)){
                assertEquals(0L, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(date), statusRow.getDetails().getLastExtract());
                assertEquals(StoreStatus.WARNING, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(outdatedStore)){
                assertEquals(normalInvoiceCount, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(outdatedDate), statusRow.getDetails().getLastExtract());
                assertEquals(StoreStatus.ERROR, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(uptoDateStore)){
                assertEquals(normalInvoiceCount, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(date), statusRow.getDetails().getLastExtract());
                assertEquals(StoreStatus.OK, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(inactiveStore)){
                assertEquals("false", statusRow.getActive());
                assertEquals(StoreStatus.WARNING, statusRow.getStatus());
            }
            assertReason(statusRow);
        }
    }
    
    /**
     * Test case:
     *  Executing a query for stores with WARNING or OK status. 
     *  
     * Expected:
     *  Returns all entities except the 12th, as that's an OK entity.
     */
    @Test
    public void testFilteringByStatus() throws InvalidStatusRequestException{
        
        int skip = 5;
        int take = 7;

        final Long okStore = 12L;

        // Prepare list for store DAO
        List<StoreEntity> stores = new ArrayList<StoreEntity>();
        for(long i = 1; i <= 30; i++){
            stores.add(mockStore(i, API_ID));
        }
        
        // Set Dao to return all
        StoreDao mockDao = mockStoreDao(stores);
        target.setSourceDao(mockDao);
        
        // Returns good date only for OK store
        ExtractorMonitoringService extractorMonitor = mock(ExtractorMonitoringService.class);
        when(extractorMonitor.getLastSuccessfulExecution(eq(okStore))).thenReturn(new Date());
        when(extractorMonitor.getLastSuccessfulExecution(not(eq(okStore)))).thenReturn(new Date(0L));
        statusService.setExtractorMonitoringService(extractorMonitor);
        
        // Returns 500 recent invoices only for OK store
        InvoiceCountMonitoringService invoiceMonitor = mock(InvoiceCountMonitoringService.class);
        when(invoiceMonitor.getInvoiceCount(eq(okStore))).thenReturn(500L);
        when(invoiceMonitor.getLastInvoiceDate(eq(okStore))).thenReturn(new Date());
        when(invoiceMonitor.getInvoiceCount(not(eq(okStore)))).thenReturn(0L);
        when(invoiceMonitor.getLastInvoiceDate(not(eq(okStore)))).thenReturn(new Date(0L));
        statusService.setInvoiceCountService(invoiceMonitor);

        // Prepare query
        StatusQueryEntity config = new StatusQueryEntity();
        config.setSkip(5);
        config.setTake(8);
        config.setStatus(new HashSet<StoreStatus>(Arrays.asList(StoreStatus.WARNING, StoreStatus.ERROR)));

        // Act
        List<StatusEntity> retVal = target.getStoreStatuses(config);
        
        // Expecting entities 6,7,8,9,10,11,13,14
        assertEquals(config.getTake(), retVal.size());
        
        // What store ids were returned?
        Set<Long> storeIds = new HashSet<Long>();
        for(StatusEntity status : retVal){
            storeIds.add(status.getStoreId());
        }
        
        assertFalse("OK entities should've been skipped", storeIds.contains(okStore));
        assertTrue("14th entity should've been returned", storeIds.contains(new Long(skip+take+1)));
    }
    
    /**
     * Test case:
     *  Requesting status for stores and specifying an api
     *  value which is not associated with the gateway     *  
     * @throws InvalidStatusRequestException 
     */
    @Test(expected = InvalidStatusRequestException.class)
	public void testNotGatewayApi() throws InvalidStatusRequestException{

        // Set Dao to return 3 stores, 2 with gateway related api, 1 other
        StoreDao mockDao = mockStoreDao(Arrays.asList(mockStore(1L, API_ID), mockStore(2L, OTHER_API_ID), mockStore(3L, API_ID)));
        target.setSourceDao(mockDao);
        

        // Set query configuration
        StatusQueryEntity config = new StatusQueryEntity();
        config.setApi(new HashSet<String>(Arrays.asList(apiService.findById(OTHER_API_ID).getApiName())));
                
        // Act
        target.getStoreStatuses(config);
	}
}
