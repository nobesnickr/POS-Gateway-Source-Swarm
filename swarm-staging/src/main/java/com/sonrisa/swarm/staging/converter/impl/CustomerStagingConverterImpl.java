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
package com.sonrisa.swarm.staging.converter.impl;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.util.IdConverter;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.staging.converter.CustomerStagingConverter;
import com.sonrisa.swarm.staging.service.CustomerStagingService;

/**
 * Class converting {@link CustomerStage} to {@link CustomerEntity}
 * @author sonrisa
 *
 */
@Service
public class CustomerStagingConverterImpl extends BaseStagingConverterImpl<CustomerStage, CustomerEntity> implements CustomerStagingConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerStagingConverterImpl.class);
    
    /** DAO of customers in the data warehouse (aka legacy DB). */
    @Autowired
    private CustomerDao dao;
    
    @Autowired
    private CustomerStagingService customerStagingService;
    
    /**
     * {@inheritDoc }
     * 
     * @param stgCust
     * @return 
     */
    @Override
    public StageAndLegacyHolder<CustomerStage, CustomerEntity> convert(CustomerStage stgCust) {
        CustomerEntity customer = null;

        final StoreEntity store = customerStagingService.findStore(stgCust);
        if (store == null) {
            LOGGER.debug("Staging customer can not be saved because its store does not exists: " + stgCust);
        } else {
            try {
                final Long foreignCustomerId = Long.parseLong(stgCust.getLsCustomerId());
                customer = findOrCreateCustomer(store.getId(), foreignCustomerId);
            } catch (NumberFormatException e){
                final String errorMsg = "Illegal foreign id: " + stgCust.getLsCustomerId();
                LOGGER.debug("Failed to convert CustomerStage to CustomerEntity because: {}", errorMsg, e);
                return new StageAndLegacyHolder<CustomerStage, CustomerEntity>(stgCust, errorMsg);
            }

            // performs mapping between staging customer and destination customer object
            copyStgCustomer(stgCust, customer);
            customer.setLsCustomerId(IdConverter.positiveCustomerId(stgCust.getLsCustomerId()));
            customer.setStore(store);   // sets the reference to the store
        }
               
        return  new StageAndLegacyHolder<CustomerStage, CustomerEntity>(customer, stgCust);
    }

    public void setCustomerStagingService(CustomerStagingService customerStagingService) {
		this.customerStagingService = customerStagingService;
	}

	public void setDao(CustomerDao dao) {
		this.dao = dao;
	}

	/**
     * Performs mapping between staging customer and destination customer object.
     * 
     * @param stgCust
     * @param custEntity 
     */
    private void copyStgCustomer(final CustomerStage stgCust, final CustomerEntity custEntity){
        dozerMapper.map(stgCust, custEntity);                
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
		this.dozerMapper = dozerMapper;
	}

	/**
     * Find a customer by his storeId and foreign customerId. 
     * (The foreign customerId is the ID that identifies the customer in the source system.)
     * 
     * @param storeId
     * @param foreignCustomerId
     * @param stgCust
     * @return 
     */
    private CustomerEntity findOrCreateCustomer(final Long storeId, final Long foreignCustomerId) {
		CustomerEntity customer = dao.findByStoreAndForeignId(storeId, IdConverter.positiveCustomerId(foreignCustomerId));
        if (customer == null){
            LOGGER.debug("Staging customer can not be found in the Data warehouse so a new one will be created. StoreId " 
                    + storeId + " foreign customerId: " + foreignCustomerId);
            customer = new CustomerEntity();                        
        }else{
            LOGGER.debug("Staging customer has been found in the Data warehouse so it will be updated. StoreId " 
                    + storeId + " foreign customerId: " + foreignCustomerId);
        }
        return customer;
    }
}
