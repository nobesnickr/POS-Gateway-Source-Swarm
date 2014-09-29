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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.staging.service.CustomerStagingService;

public class CustomerStagingConverterImplTest {
	
	/**
     * Target being tested
     */
    private CustomerStagingConverterImpl target;
    
    /**
     * mock service to retrieve a store;
     */
    private CustomerStagingService mockCustomerService;
	
	/**
	 * used for mocking store
	 */
	private final static Long STORE_ID = 434L;
	
	/**
	 * returns null when requested for a customer (not exists)
	 */
	private CustomerDao mockCustomerDao;

    /**
     * Dummy store for the invoice
     */
    private StoreEntity dummyStore;
    
    /** Mock dozer mapper */
    private DozerBeanMapper mockDozerMapper;

	@Before
	public void setUp() {
		target = new CustomerStagingConverterImpl();
		dummyStore = new StoreEntity();
		dummyStore.setId(STORE_ID);
		
		mockCustomerService = mock(CustomerStagingService.class);
		when(mockCustomerService.findStore(any(BaseStageEntity.class))).thenReturn(dummyStore);
		target.setCustomerStagingService(mockCustomerService);
		
		mockCustomerDao = mock(CustomerDao.class);
		when(mockCustomerDao.findByStoreAndForeignId(any(Long.class), any(Long.class))).thenReturn(null);
		target.setDao(mockCustomerDao);
		
		mockDozerMapper = mock(DozerBeanMapper.class);
        doAnswer(new Answer<CustomerEntity>() {

			@Override
			public CustomerEntity answer(InvocationOnMock invocation) throws Throwable {
				CustomerStage arg = (CustomerStage) (invocation.getArguments()[0]);
				CustomerEntity retval = (CustomerEntity) (invocation.getArguments()[1]);
	            
	            if(retval == null){
	            	retval = new CustomerEntity();
	            }
	            
	            if (StringUtils.hasLength(arg.getLsCustomerId())) {
	                retval.setLsCustomerId(Long.parseLong(arg.getLsCustomerId()));
	            }

	            return retval;
			}
		}).when(mockDozerMapper).map(any(), any(CustomerEntity.class));
        target.setDozerMapper(mockDozerMapper);
	}

	/**
	 * Test case:
	 * create a staging customer with negative id.
	 * Expected: 
	 * the customer has positive id at the end of conversion 
	 */
	@Test
	public void testNegativeRemoteId() {
		CustomerStage testCustomer = new CustomerStage();
		testCustomer.setLsCustomerId("-145");
		testCustomer.setStoreId(dummyStore.getId());
		StageAndLegacyHolder<CustomerStage, CustomerEntity> holder = target.convert(testCustomer);
		assertEquals(Long.valueOf(145L), holder.getLegacyEntity().getLsCustomerId());
	}
}
