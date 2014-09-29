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
package com.sonrisa.swarm.staging.converter.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.staging.converter.TimeZoneService;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;

/**
 * Class testing the {@link InvoiceStagingConverterImpl} test
 */
public class InvoiceStagingConverterImplTest {

    final static Long STORE_ID = 456L;
    
    /**
     * Target being tested
     */
    private InvoiceStagingConverterImpl target;
    
    /**
     * Mock invoice DAO returning no invoices
     */
    private InvoiceDao mockInvoiceDao;
    
    /**
     * Dummy store for the invoice
     */
    private StoreEntity dummyStore;
    
    /**
     * Mock invoice staging service
     */
    private InvoiceStagingService mockInvoiceStagingService;
    
    /** Mock dozer mapper */
    private DozerBeanMapper mockDozerMapper;
    
    /** Mock customer DAO returning dummyCustomer */
    private CustomerDao mockCustomerDao;

    @SuppressWarnings("serial")
    @Before
    public void resetTarget(){
        
        target = new InvoiceStagingConverterImpl();
        
        mockInvoiceDao = mock(InvoiceDao.class);
        when(mockInvoiceDao.findByStoreAndForeignId(any(Long.class),any(Long.class))).thenReturn(null);
        target.setInvoiceDao(mockInvoiceDao);
        
        dummyStore = new StoreEntity(){
            public Long getId() {
                return STORE_ID;
            }
        };
        
        mockInvoiceStagingService = mock(InvoiceStagingService.class);
        when(mockInvoiceStagingService.findStore(any(BaseStageEntity.class))).thenReturn(dummyStore);
        target.setInvoiceStagingService(mockInvoiceStagingService);
                
        mockCustomerDao = mock(CustomerDao.class);
        when(mockCustomerDao.findByStoreAndForeignId(any(Long.class), any(Long.class))).thenAnswer(new MockCustomerDaoAnswer());
        target.setCustomerDao(mockCustomerDao);
                
        mockDozerMapper = mock(DozerBeanMapper.class);
        doAnswer(new MockDozerAnswer()).when(mockDozerMapper).map(any(), any(InvoiceEntity.class));
        target.setDozerMapper(mockDozerMapper);
        
        TimeZoneService mockTimezoneService = mock(TimeZoneService.class);
        target.setTimeZoneService(mockTimezoneService);
        
        target.setInvoiceStagingFilters(new ArrayList<InvoiceStagingFilter>());
        
    }
        
    /**
     * Test case:
     *  Converting an {@link InvoiceStage}
     *  
     * Expected:
     *  Its customer_id is copied value of the the legacy customer id
     */
    @Test
    public void testLegacyCustomerIdUsed(){
      final Long lsCustomerId = 12345L;
      
      InvoiceStage input = new InvoiceStage();
      input.setLsInvoiceId("987654321");
      input.setLsCustomerId(lsCustomerId.toString());
      
      // Act
      StageAndLegacyHolder<InvoiceStage, InvoiceEntity> result = target.convert(input);
      assertEquals(lsCustomerId, result.getLegacyEntity().getLsCustomerId());
    }
    
    /**
     * Test case:
     *  Converting an {@link InvoiceStage} with lsCustomerId missing
     *  
     * Expected:
     *  Its customer_id is set to 0, because its not a nullable legacy schema field
     */
    @Test
    public void testMissingLegacyCustomerId(){
      final Long lsCustomerId = 12345L;
      
      InvoiceStage input = new InvoiceStage();
      input.setLsInvoiceId("987654321");
      input.setLsCustomerId(null);
      
      // Act
      StageAndLegacyHolder<InvoiceStage, InvoiceEntity> result = target.convert(input);
      assertEquals((Long)0L, result.getLegacyEntity().getLsCustomerId());
    }
    
    /**
     * Test case:
     *  Converting an {@link InvoiceStage}, with total set
     *  
     * Expected:
     *  The created {@link InvoiceEntity}'s total matches the stage value
     */
    @Test
    public void testInvoiceTotalHasLegacyValue(){
      final BigDecimal total = new BigDecimal("879.99");
      
      InvoiceStage input = new InvoiceStage();
      input.setLsInvoiceId("987654321");
      input.setTotal(total.toPlainString());
      
      // Act
      StageAndLegacyHolder<InvoiceStage, InvoiceEntity> result = target.convert(input);
      assertEquals(total, result.getLegacyEntity().getTotal());
    }

    /**
     * Test case:
     *  Converting an {@link InvoiceStage}, with a legacy
     *  customer entity in the customers table matching its
     *  legacy customer id
     *  
     * Expected:
     *  The invoices mainphone, email and name is read from
     *  the customer entity
     */
    @Test
    public void testCustomerFieldsOnInvoice(){
      final Long lsCustomerId = 555L;
      
      InvoiceStage input = new InvoiceStage();
      input.setLsInvoiceId("987654321");
      input.setLsCustomerId(lsCustomerId.toString());
      
      // Act
      StageAndLegacyHolder<InvoiceStage, InvoiceEntity> result = target.convert(input);
      assertEquals(MockCustomerDaoAnswer.NAME, result.getLegacyEntity().getCustomerName());
      assertEquals(MockCustomerDaoAnswer.PHONE, result.getLegacyEntity().getCustomerPhone());
      assertEquals(MockCustomerDaoAnswer.EMAIL, result.getLegacyEntity().getCustomerEmail());
      
    }
    
    /**
     * Test case:
     *  Converting multiple {@link InvoiceStage}, with a legacy
     *  the linesProcessed field missing, 0 or 1
     *  
     * Expected:
     *  If fields is missing, a default value is used,
     *  otherwise it's copied onto the {@link InvoiceEntity}
     */
    @Test
    public void testLinesProcessed(){
        
      InvoiceStage missingLinesProcessed = new InvoiceStage();
      missingLinesProcessed.setLsInvoiceId("987654321");
      missingLinesProcessed.setLinesProcessed(null);
      
      InvoiceStage linesProcessed = new InvoiceStage();
      linesProcessed.setLsInvoiceId("123456789");
      linesProcessed.setLinesProcessed("1");
      
      InvoiceStage linesNotProcessed = new InvoiceStage();
      linesNotProcessed.setLsInvoiceId("5555555");
      linesNotProcessed.setLinesProcessed("0");
            
      assertLegacyLinesProcessed(Boolean.TRUE, target.convert(missingLinesProcessed));
      assertLegacyLinesProcessed(Boolean.TRUE, target.convert(linesProcessed));
      assertLegacyLinesProcessed(Boolean.FALSE, target.convert(linesNotProcessed));
    }
    
    
    /**
     * Test case:
     *  Various invoice filters are set for the converter
     *  
     * Expected:
     *  The converter acts accordingly to the filters's results
     */
    @Test
    public void testStagingFiltersForInvoice() throws Exception {
     
        InvoiceStagingFilter rejectNullInvoiceNumber = new InvoiceStagingFilter() {
            public StagingFilterValue approve(InvoiceStage entity) {
                if("0".equals(entity.getLsInvoiceId())){
                    return StagingFilterValue.RETAINABLE;
                } else {
                    return StagingFilterValue.APPROVED;
                }
            }
        };
        
        InvoiceStagingFilter flagNullTender = new InvoiceStagingFilter() {
            public StagingFilterValue approve(InvoiceStage entity) {
                if("0".equals(entity.getTender())){
                    return StagingFilterValue.MOVABLE_WITH_FLAG;
                } else {
                    return StagingFilterValue.APPROVED;
                }
            }
        };
        
        target.setInvoiceStagingFilters(Arrays.asList(rejectNullInvoiceNumber,flagNullTender));
        
        InvoiceStage okCustomer = new InvoiceStage();
        okCustomer.setLsInvoiceId("987654321");
        okCustomer.setLsCustomerId("111");
        okCustomer.setTender("777");
        
        // no tender & null invoice
        InvoiceStage flagableAndRetainable = new InvoiceStage();
        flagableAndRetainable.setLsInvoiceId("0");
        flagableAndRetainable.setLsCustomerId("222");
        flagableAndRetainable.setTender("0");
        
        // no tender
        InvoiceStage flagable = new InvoiceStage();
        flagable.setLsInvoiceId("865");
        flagable.setLsCustomerId("222");
        flagable.setTender("0");
        
        // Act
        StageAndLegacyHolder<InvoiceStage, InvoiceEntity> okResult = target.convert(okCustomer);
        StageAndLegacyHolder<InvoiceStage, InvoiceEntity> flagableResult = target.convert(flagable);
        StageAndLegacyHolder<InvoiceStage, InvoiceEntity> flagableAndRetainableResult = target.convert(flagableAndRetainable);
        
        // Assert
        assertNotNull(okResult.getLegacyEntity());
        assertEquals(Boolean.TRUE, okResult.getLegacyEntity().getCompleted());
        
        assertNotNull(flagableResult.getLegacyEntity());
        assertEquals(Boolean.FALSE, flagableResult.getLegacyEntity().getCompleted());
        
        assertNull(flagableAndRetainableResult.getLegacyEntity());
    }
    
    /**
     * Asserts the legacy entity's linesProcessed
     */
    private void assertLegacyLinesProcessed(Boolean expected, StageAndLegacyHolder<InvoiceStage, InvoiceEntity> actual){
        assertEquals(expected, actual.getLegacyEntity().getLinesProcessed());
    }
    
    /**
     * Mocked dozer
     */
    private static class MockDozerAnswer implements Answer<InvoiceEntity> {
        @Override
        public InvoiceEntity answer(InvocationOnMock invocation) throws Throwable {
            InvoiceStage arg = (InvoiceStage) (invocation.getArguments()[0]);
            InvoiceEntity retval = (InvoiceEntity) (invocation.getArguments()[1]);
            
            if(retval == null){
            	retval = new InvoiceEntity();
            }
            
            if (StringUtils.hasLength(arg.getLsCustomerId())) {
                retval.setLsCustomerId(Long.parseLong(arg.getLsCustomerId()));
            }

            if (StringUtils.hasLength(arg.getTotal())) {
                retval.setTotal(new BigDecimal(arg.getTotal()));
            }

            return retval;
        }
    }
    
    private static class MockCustomerDaoAnswer implements Answer<CustomerEntity> {

        private static final String NAME = "Testing Tom";
        private static final String PHONE = "Testing Tom";
        private static final String EMAIL = "Testing Tom";

		@Override
		public CustomerEntity answer(InvocationOnMock invocation) throws Throwable {
			Long storeId = (Long) (invocation.getArguments()[0]);
			Long lsCustomerId = (Long) (invocation.getArguments()[1]);

	        // Invoice converter will attempt to fill out email, mainphone etc.
	        // based on already existing customer in the legacy tables
	        CustomerEntity dummyCustomer = new CustomerEntity();
	        dummyCustomer.setLsCustomerId(lsCustomerId);
			dummyCustomer.setName(NAME);
	        dummyCustomer.setPhone(PHONE);
	        dummyCustomer.setEmail(EMAIL);
			return dummyCustomer;
		}
    }
}

