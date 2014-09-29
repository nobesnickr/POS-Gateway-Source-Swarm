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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.admin.model.RpStatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.model.query.RpStatusQueryEntity;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.admin.service.impl.RpStatusProcessingServiceImpl;
import com.sonrisa.swarm.admin.service.impl.RpStatusServiceImpl;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;
import com.sonrisa.swarm.posintegration.service.impl.ApiServiceImpl;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.dao.impl.RpPluginDao;
import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity.RpStoreState;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;

/**
 * Class testing the {@link RpStatusServiceImpl}
 * @author Barnabas
 */
public class RpStatusServiceImplTest extends BaseStatusServiceTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpStatusServiceImplTest.class);

    /**
     * Target to be tested
     */
    private RpStatusServiceImpl target;
    
    /**
     * Processing service
     */
    private RpStatusProcessingServiceImpl rpStatusProcessor;
        
    /**
     * API service impl containing retailpro8 and retailpro9
     */
    private ApiServiceImpl mockApiService;

    /**
     * Setup tests
     */
    @Before
    public void setUp(){
        mockApiService = new ApiServiceImpl();
        mockApiService.fillCache(Arrays.asList(
                new ApiEntity(10L, "retailpro8", ApiType.RETAILPRO_API),
                new ApiEntity(11L, "retailpro9", ApiType.RETAILPRO_API)
        ));
                
        RpStoreDao storeDao = mock(RpStoreDao.class);

        rpStatusProcessor = new RpStatusProcessingServiceImpl();
        rpStatusProcessor.setPluginDao(mockRpPluginDao(Arrays.asList(new RpPluginEntity())));
        rpStatusProcessor.setInvoiceCountService(mockInvoiceCountMonitoringService());  
        rpStatusProcessor.setLogService(mockLogMonitor("error-swarm-id"));
        
        target = new RpStatusServiceImpl(rpStatusProcessor, storeDao, mockApiService); 
        target.setGlobalQueryLimit(1000);
        target.setApiService(mockApiService);
        target.setStatusProcessingService(rpStatusProcessor);
        target.setIgnoredStoreNames("");
    }
    
    /**
     * Test case: There is a retail pro store in the database
     * 
     * Expected: Its returned by the service and its top-level fields are as expected
     */
    @Test
    public void testCommonFields() throws InvalidStatusRequestException{
        
        final RpStoreEntity store = mockRpStore(1L, 100L, "retailpro8");
        
        // StoreDao returns this
        RpStoreDao mockDao = mock(RpStoreDao.class);
        when(mockDao.find(any(JpaFilter.class),anyLong(),anyLong())).thenReturn(
            Arrays.asList(store)             
        );
        
        target.setSourceDao(mockDao);
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();
        
        // Act
        List<RpStatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert
        SimpleDateFormat dateFormat = new SimpleDateFormat(StatusProcessingService.STORE_STATUS_DATE_FORMAT);
        
        assertEquals(1, retVal.size());
        assertEquals(store.getStoreId(), retVal.get(0).getStoreId());
        assertEquals(store.getSwarmId(), retVal.get(0).getSwarmId());
        assertEquals(store.getTimeZone(), retVal.get(0).getTimezone());
        assertEquals(store.getTimeOffset(), retVal.get(0).getTimeOffset());
        assertEquals(store.getPosSoftware(), retVal.get(0).getApi());
        assertEquals(store.getNotes(), retVal.get(0).getNotes());
        assertEquals(dateFormat.format(store.getCreated()), retVal.get(0).getCreated());
    }
    
    /**
     * Test case:
     *  Requesting status for retail pro stores without specifying which apis
     * 
     * Expected:
     *  Only Retail Pro apis are returned
     */
    @Test
    public void testOnlyGatewayApisAreReturned() throws InvalidStatusRequestException{
        
        // Set Dao to return 3 stores, 2 with gateway related api, 1 other
        RpStoreDao mockDao = mockRpStoreDao(Arrays.asList(
                mockRpStore(1L, 1L, "retailpro8"), mockRpStore(2L, 2L, "retailpro9"), mockRpStore(3L, 3L, "vend-os")));
        
        target.setSourceDao(mockDao);
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();
                
        // Act
        target.getStoreStatuses(config);
        
        // Verify that WHERE api_id = POS_API was added to query
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        
        // Retail Pro APIs
        List<String> expected = new ArrayList<String>(mockApiService.findApiNamesByType(ApiType.RETAILPRO_API));
        
        // Also as the default API (retailpro8) should be returned as well, we expect the empty and null to be returned too
        expected.add(null);
        expected.add("");
        
        assertQueryParam("posSoftware", expected, "IN", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }
    
    /**
     * Test case:
     *  Requesting status for retail pro stores without specified api name
     * 
     * Expected:
     *  Only the specified API is returned
     */
    @Test
    public void testFilterByPosSoftware() throws InvalidStatusRequestException {
        
        // One from the Retail Pro apis, but not "retailpro8" because that is the default
        final String filteredApi = "retailpro9";
        
        // Set Dao to return 3 stores, 2 with gateway related api, 1 other
        RpStoreDao mockDao = mockRpStoreDao(Arrays.asList(
                mockRpStore(1L, 1L, filteredApi), mockRpStore(2L, 2L, "retailpro9"), mockRpStore(3L, 3L, "vend-os")));
        
        target.setSourceDao(mockDao);
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();
        config.setApi(new HashSet<String>(Arrays.asList(filteredApi)));
                
        // Act
        target.getStoreStatuses(config);
        
        // Verify that WHERE api_id = POS_API was added to query
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("posSoftware", Arrays.asList(filteredApi), "IN", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }
    
    /**
     * Test case:
     *  Requesting status for retail pro stores without specifying which swarm id
     * 
     * Expected:
     *  Only Retail Pro APIs are returned
     */
    @Test
    public void testFilteringBySwarmId() throws InvalidStatusRequestException{
        
        final String swarmId = "sonrisa-tst";
        
        // Set DAO to return 3 stores, 2 with gateway related api, 1 other
        RpStoreDao mockDao = mockRpStoreDao(Arrays.asList(mockRpStore(1L, 1L, "retailpro8")));
        target.setSourceDao(mockDao);
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();
        config.setSwarmId(swarmId);
                
        // Act
        target.getStoreStatuses(config);
        
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("swarmId", swarmId, "=", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }
    

    /**
     * Test case:
     *  Requesting status for retail pro stores
     * 
     * Expected:
     *  Stores with names in ignored store names 
     *  are excluded
     */
    @Test
    public void testIgnoringStoreNames() throws InvalidStatusRequestException{
        
        RpStoreDao mockDao = mockRpStoreDao(Arrays.asList(mockRpStore(1L, 1L, "retailpro8")));
        target.setSourceDao(mockDao);
        
        // Set store names to be ignore
        final String[] storeNames = new String[] { "Default", "Ignore" };
        target.setIgnoredStoreNames(StringUtils.join(storeNames,","));
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();

        // Act
        target.getStoreStatuses(config);
        
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("storeName", Arrays.asList(storeNames), "NOT IN", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }

    /**
     * Test case:
     *  Requesting status for Retail Pro stores
     * 
     * Expected:
     *  Adding filter to the query which ignores stores
     *  with store status IGNORED
     */
    @Test
    public void testIgnoringStores() throws InvalidStatusRequestException{
        
        RpStoreDao mockDao = mockRpStoreDao(Arrays.asList(mockRpStore(1L, 1L, "retailpro8")));
        target.setSourceDao(mockDao);
        
        // Set query configuration
        RpStatusQueryEntity config = new RpStatusQueryEntity();

        // Act
        target.getStoreStatuses(config);
        
        ArgumentCaptor<JpaFilter> filterCaptor = ArgumentCaptor.forClass(JpaFilter.class);
        verify(mockDao, times(1)).find(filterCaptor.capture(),anyLong(),anyLong());
        
        assertQueryParam("state", RpStoreState.NORMAL, "=", (SimpleFilter<StoreEntity>)filterCaptor.getValue());
    }
    
    /**
     * Test case: 
     *  We have a store in the database with heartbeat received
     *  after the last error.
     *  
     * Expected;
     *  It's status is only warning
     *  
     */
    @Test
    public void testTemporaryClientErrorIsOnlyWarning() throws InvalidStatusRequestException {

        final Long storeId = 1L;
        
        final Date now = new Date();
        
        target.setSourceDao(mockRpStoreDao(Arrays.asList(mockRpStore(storeId))));
        
        // Heartbeat was received just now
        List<RpPluginEntity> plugins = Arrays.asList(mockRpPlugin(mockSwarmId(storeId), 1L, now, "1.6.3.0"));
        rpStatusProcessor.setPluginDao(mockRpPluginDao(plugins));

        // Invoice was received just now
        InvoiceCountMonitoringService invoiceMonitor = mock(InvoiceCountMonitoringService.class);
        when(invoiceMonitor.getInvoiceCount(eq(storeId))).thenReturn(500L);
        when(invoiceMonitor.getLastInvoiceDate(eq(storeId))).thenReturn(new Date());
        
        // Set log monitoring service, to return an error from an hour age
        final String swarmId = mockSwarmId(storeId);
        RpLogEntity logEntity = mockLogEntity();
        logEntity.setServerTimestamp(new Date(now.getTime() - 3600L * 1000));
        
        // This log entity is returned
        RpLogMonitoringService logMonitoringService = mock (RpLogMonitoringService.class);
        when(logMonitoringService.getRecentClientError(eq(swarmId))).thenReturn(logEntity);
        rpStatusProcessor.setLogService(logMonitoringService);
        
        // Act
        RpStatusQueryEntity config = new RpStatusQueryEntity();
        List<RpStatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert
        assertEquals(1, retVal.size());
        assertEquals(storeId, retVal.get(0).getStoreId());
        assertEquals(StoreStatus.WARNING, retVal.get(0).getStatus());
    }

    /**
     * Test case: 
     *  We have five stores in the database
     *  
     * Expected:
     *  Status values and reason match the specification
     *  
     */
    @Test
    public void testRpStatusDetails() throws InvalidStatusRequestException {

        final Long emptyStore = 1L; // No invoices
        final Long idleStore = 2L; // No heartbeat received
        final Long okStore = 3L;
        final Long errorStore = 4L;
        final Long noTimezone = 5L;
        
        final Date longTimeAgo = new Date(1000000000L * 1000L); // Sun, 09 Sep 2001 01:46:40 GMT
        final Date now = new Date();
        
        final long normalInvoiceCount = 500;
        
        // Mocking store dao
        RpStoreEntity noTimezoneStore = mockRpStore(noTimezone);
        noTimezoneStore.setTimeZone(null);
        
        RpStoreDao mockDao = mockRpStoreDao(
                Arrays.asList(mockRpStore(emptyStore), mockRpStore(idleStore), mockRpStore(okStore), mockRpStore(errorStore), noTimezoneStore)
        );
        target.setSourceDao(mockDao);
        
        // Mocking heartbeat dao
        RpPluginEntity idlePluginEntity = new RpPluginEntity();
        idlePluginEntity.setId(1L);
        
        List<RpPluginEntity> plugins = Arrays.asList(
                mockRpPlugin(mockSwarmId(emptyStore), 1L, now, "1.6.3.0"),
                mockRpPlugin(mockSwarmId(idleStore), 10L, longTimeAgo, "1.6.7.0"),
                mockRpPlugin(mockSwarmId(okStore), 100L, now, "1.6.9.0"),
                mockRpPlugin(mockSwarmId(errorStore), 200L, now, "1.8.9.0"),
                mockRpPlugin(mockSwarmId(noTimezone), 300L, now, "1.1.9.0"));
        
        RpPluginDao pluginDao = mockRpPluginDao(plugins);
        rpStatusProcessor.setPluginDao(pluginDao);
        
        InvoiceCountMonitoringService invoiceMonitor = mock(InvoiceCountMonitoringService.class);
        when(invoiceMonitor.getInvoiceCount(not(eq(emptyStore)))).thenReturn(normalInvoiceCount);
        when(invoiceMonitor.getLastInvoiceDate(not(eq(emptyStore)))).thenReturn(new Date());
        when(invoiceMonitor.getInvoiceCount(eq(emptyStore))).thenReturn(0L);
        when(invoiceMonitor.getLastInvoiceDate(eq(emptyStore))).thenReturn(new Date(0L));
        rpStatusProcessor.setInvoiceCountService(invoiceMonitor);
        
        // Set log monitoring service
        rpStatusProcessor.setLogService(mockLogMonitor(mockSwarmId(errorStore)));
        
        // Act
        RpStatusQueryEntity config = new RpStatusQueryEntity();
        List<RpStatusEntity> retVal = target.getStoreStatuses(config);
        
        // Assert
        assertEquals(5, retVal.size());
        for(RpStatusEntity statusRow : retVal){
            LOGGER.debug("Asserting {}", statusRow);
            if(statusRow.getStoreId().equals(emptyStore)){
                assertEquals(0L, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(now), statusRow.getDetails().getLastHeartbeat());
                assertEquals(plugins.get(0).getPluginVersion(), statusRow.getDetails().getClientVersion());
                assertEquals(StoreStatus.WARNING, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(idleStore)){
                assertEquals(normalInvoiceCount, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(longTimeAgo), statusRow.getDetails().getLastHeartbeat());
                assertEquals(plugins.get(1).getPluginVersion(), statusRow.getDetails().getClientVersion());
                assertEquals(StoreStatus.ERROR, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(okStore)){
                assertEquals(normalInvoiceCount, statusRow.getDetails().getInvoiceCount());
                assertEquals(dateFormat.format(now), statusRow.getDetails().getLastHeartbeat());
                assertEquals(plugins.get(2).getPluginVersion(), statusRow.getDetails().getClientVersion());
                assertNotNull(statusRow.getTimezone());
                assertNotNull(statusRow.getTimeOffset());
                assertEquals(StoreStatus.OK, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(errorStore)){
                assertEquals(StoreStatus.ERROR, statusRow.getStatus());
            } else if(statusRow.getStoreId().equals(noTimezone)){
                assertNull(statusRow.getTimezone());
                assertEquals(StoreStatus.WARNING, statusRow.getStatus());
            }
            assertReason(statusRow);
        }
    }
}
