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
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sonrisa.swarm.admin.model.BaseStatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.legacy.dao.StoreDao;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.InvoiceCountMonitoringService;
import com.sonrisa.swarm.retailpro.dao.RpStoreDao;
import com.sonrisa.swarm.retailpro.dao.impl.RpPluginDao;
import com.sonrisa.swarm.retailpro.model.RpLogEntity;
import com.sonrisa.swarm.retailpro.model.RpPluginEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.service.RpLogMonitoringService;

/**
 * Base class for testing status services
 * 
 * @author Barnabas
 */
public abstract class BaseStatusServiceTest {

    /**
     * Date format for formatting dates
     */
    protected SimpleDateFormat dateFormat = new SimpleDateFormat(StatusProcessingService.STORE_STATUS_DATE_FORMAT);
    
    /**
     * Create mock {@link StoreDao} which returns a few stores 
     */
    protected StoreDao mockStoreDao(int numberOfStores, Long apiId){
        
        List<StoreEntity> stores = new ArrayList<StoreEntity>();
        for(long i = 0; i < numberOfStores; i++) {
            stores.add(mockStore(i, apiId));
        }

        return mockStoreDao(stores);
    }
    
    /**
     * Create mock {@link StoreDao} which returns a few stores 
     */
    protected StoreDao mockStoreDao(List<StoreEntity> stores){
        // StoreDao returns this
        StoreDao mockDao = mock(StoreDao.class);
        when(mockDao.find(any(JpaFilter.class))).thenReturn(new ArrayList<StoreEntity>(stores));
        when(mockDao.find(any(JpaFilter.class),anyLong(),anyLong())).thenReturn(new ArrayList<StoreEntity>(stores));
        
        return mockDao;
    }
    
    /**
     * Creates a mock {@link RpStoreDao} which returns the stores specified
     * @param stores
     * @return
     */
    protected RpStoreDao mockRpStoreDao(List<RpStoreEntity> stores){
        // StoreDao returns this
        RpStoreDao mockDao = mock(RpStoreDao.class);
        when(mockDao.find(any(JpaFilter.class),anyLong(),anyLong())).thenReturn(stores);
        return mockDao;
    }
    
    /**
     * Create mock {@link StoreEntity}
     */
    protected StoreEntity mockStore(Long storeId, Long apiId){
       // StoreEntity with random data
        StoreEntity store = new StoreEntity();
        store.setCreated(new Date());
        store.setId(storeId);
        store.setName("Sonrisa - Test ~~ " + storeId);
        store.setApiId(apiId);
        store.setNotes("Test - Sonrisa");
        store.setActive(Boolean.TRUE);
        return store;
    }
    
    /**
     * Get mock swarm id for rpStore
     * @param rpStoreId
     * @return
     */
    protected String mockSwarmId(Long rpStoreId){
        return "sonrisa-test-" + rpStoreId;
    }
    
    /**
     * Create mock {@link RpStoreEntity}
     */
    protected RpStoreEntity mockRpStore(Long rpStoreId, Long storeId, String posSoftware){
        final RpStoreEntity rpStore = new RpStoreEntity();
        rpStore.setSwarmId(mockSwarmId(rpStoreId));
        rpStore.setStoreName("Store ~~ " + storeId);
        rpStore.setCreated(new Date());
        rpStore.setSbsNumber("001");
        rpStore.setStoreNumber("SON");
        rpStore.setNotes("notes");
        rpStore.setPosSoftware(posSoftware);
        rpStore.setTimeZone("US/East");
        rpStore.setTimeOffset(new Integer(0));
        rpStore.setId(rpStoreId);
        rpStore.setStoreId(storeId);
        return rpStore;
    }
    
    /**
     * Create mock {@link RpStoreEntity}
     */
    protected RpStoreEntity mockRpStore(Long id){
        return mockRpStore(id, id, "sonrisa-pos");
    }
    
    /**
     * Create mock {@link RpPluginEntity}
     */
    protected RpPluginEntity mockRpPlugin(String swarmId, Long id, Date date, String version){
        RpPluginEntity pluginEntity = new RpPluginEntity();
        pluginEntity.setId(id);
        pluginEntity.setHeartbeat(date);
        pluginEntity.setPluginVersion(version);
        pluginEntity.setSwarmId(swarmId);
        return pluginEntity;
    }

    /**
     * Create mock {@link RpPluginDao}
     */
    protected RpPluginDao mockRpPluginDao(List<RpPluginEntity> plugins){
        // StoreDao returns this
        RpPluginDao mockDao = mock(RpPluginDao.class);
        for(RpPluginEntity plugin : plugins){
            when(mockDao.findBySwarmId(eq(plugin.getSwarmId()))).thenReturn(plugin);
        }
        return mockDao;
    }
    
    /**
     * Create mock {@link InvoiceCountMonitoringService}
     */
    protected InvoiceCountMonitoringService mockInvoiceCountMonitoringService(){
        // Setup invoice monitoring service
        InvoiceCountMonitoringService invoiceMonitor = mock(InvoiceCountMonitoringService.class);
        when(invoiceMonitor.getInvoiceCount(anyLong())).thenReturn(123L);
        when(invoiceMonitor.getLastInvoiceDate(anyLong())).thenReturn(new Date());
        return invoiceMonitor;
    }
    
    /**
     * Mock current {@link RpLogEntity}
     */
    protected RpLogEntity mockLogEntity(){
        RpLogEntity logEntity = new RpLogEntity();
        logEntity.setDetails("Bad thing happend");
        logEntity.setServerTimestamp(new Date());
        return logEntity;
    }
    
    /**
     * Create mock {@link RpLogMonitoringService}
     */
    protected RpLogMonitoringService mockLogMonitor(String errorSwarmId){
        RpLogMonitoringService retVal = mock (RpLogMonitoringService.class);
        when(retVal.getRecentClientError(eq(errorSwarmId))).thenReturn(mockLogEntity());
        when(retVal.getRecentClientError(not(eq(errorSwarmId)))).thenReturn(null);
        return retVal;
    }
    
    /**
     * Asserts that reason exists if not OK, and doesn't if OK
     * @param statusRow
     */
    protected void assertReason(BaseStatusEntity statusRow){
        if(statusRow.getStatus() == StoreStatus.OK) {
            assertNull("OK doesn't need to be explained", statusRow.getReason());
        } else {
            assertFalse("Should be explained why", statusRow.getReason().isEmpty());
        }
    }

    /**
     * Asserts that parameter was added to the query
     */
    protected void assertQueryParam(String property, Object value, String operator, SimpleFilter<?> jpaFilter) {
        try {
            // TODO modify sonrisa-backend to elliminate reflection in this assertion method
            
            // SimpleFilter doens't expose its parameters, so we use reflection to access them
            Field field;
            field = SimpleFilter.class.getDeclaredField("parameters");
            field.setAccessible(true);
            
            FilterParameter[] parameters = (FilterParameter[])field.get(jpaFilter);
            if(parameters == null){
                fail("SimpleFilter.parameters is null.");
            }
            
            // FilterParameter doesn't expose its getters, so we use reflection to invoke them
            Method propertyGetter = FilterParameter.class.getDeclaredMethod("getProperty");
            Method valueGetter = FilterParameter.class.getDeclaredMethod("getObject");
            Method operatorGetter = FilterParameter.class.getDeclaredMethod("getOperator");
            propertyGetter.setAccessible(true);
            valueGetter.setAccessible(true);
            operatorGetter.setAccessible(true);
            
            boolean found = false;
            for(FilterParameter param : parameters){
                if(property.equals(propertyGetter.invoke(param,new Object[0]))){
                    final Object actualValue = valueGetter.invoke(param,new Object[0]);
                    final String actualOperator = (String)operatorGetter.invoke(param,new Object[0]);
                    
                    assertEquals("Value doesn't match for " + property, value, actualValue);
                    assertEquals("Operator doesn't match for " + property, operator, actualOperator.trim());
                    found = true;
                    break;
                }
            }
            
            assertTrue("Property not found in query: " + property, found);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        } catch (IllegalArgumentException e) {
            throw new AssertionError(e);
        } catch (SecurityException e) {
            throw new AssertionError(e);
        }
    }
}
