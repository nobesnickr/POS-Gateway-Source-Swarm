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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.OutletDao;
import com.sonrisa.swarm.legacy.dao.RegisterDao;
import com.sonrisa.swarm.legacy.util.IdConverter;
import com.sonrisa.swarm.legacy.util.InvoiceEntityUtil;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.OutletEntity;
import com.sonrisa.swarm.model.legacy.RegisterEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.retailpro.enums.RpReceiptType;
import com.sonrisa.swarm.staging.converter.InvoiceStagingConverter;
import com.sonrisa.swarm.staging.converter.TimeZoneService;
import com.sonrisa.swarm.staging.filter.InvoiceStagingFilter;
import com.sonrisa.swarm.staging.filter.StagingFilterValue;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;

/**
 * Implementation of the {@link InvoiceStagingConverter} interface
 * @author sonrisa
 *
 */
@Service
public class InvoiceStagingConverterImpl extends BaseStagingConverterImpl<InvoiceStage, InvoiceEntity> implements InvoiceStagingConverter {
    
   private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceStagingConverterImpl.class);
      
   /**
    * DAO of invoices in the data warehouse (aka legacy DB).
    */  
   @Autowired
   private InvoiceDao dao;
   
   @Autowired
   private InvoiceStagingService invoiceStagingService;
   
   @Autowired
   private CustomerDao customerDao;
   
   @Autowired
   private OutletDao outletDao;
   
   @Autowired
   private RegisterDao registerDao;
   
   @Autowired
   private TimeZoneService timeZoneService;
   
   @Autowired
   private List<InvoiceStagingFilter> invoiceStagingFilters; 
   
	/** JDBC template is required to access the legacy databases's updates table */
   @Autowired
   private JdbcTemplate jdbcTemplate;
          
   /**
    * {@inheritDoc}
    * @param stageEntity
    * @return
    */
   @Override
   public StageAndLegacyHolder<InvoiceStage, InvoiceEntity> convert(InvoiceStage stageEntity) {
       final InvoiceEntity legacyEntity = convertToLegacy(stageEntity);
       return new StageAndLegacyHolder<InvoiceStage, InvoiceEntity>(legacyEntity, stageEntity);
   }

   /**
    * Converts a staging invoice entity to a legacy entity.
    * 
    * @param stgEntity
    * @return Entity if the entity has been saved, null otherwise
    * @throws MappingException
    * @throws NumberFormatException 
    */
   private InvoiceEntity convertToLegacy(InvoiceStage stgEntity) throws MappingException, NumberFormatException {        
       InvoiceEntity newInvoice = null;
       
       // checks whether its store exists
       final StoreEntity store = invoiceStagingService.findStore(stgEntity);
       if (store == null) {
           LOGGER.debug("Staging invoice can not be saved because its store does not exists: " + stgEntity);
       } else {           
           // OK, store exists
           newInvoice = new InvoiceEntity();

           // Is invoice already in legacy?
           final Long foreignId = Long.parseLong(stgEntity.getLsInvoiceId());
           final InvoiceEntity existingInvoice = dao.findByStoreAndForeignId(store.getId(), foreignId);
           if(existingInvoice != null){
               LOGGER.debug("Found invoice in legacy DB, it will be updated: {}", stgEntity);
               newInvoice.setId(existingInvoice.getId());
           }
           
           // Recovering outlet
           if(stgEntity.getOutletId()!= null){
		       Long foreignOutletId = Long.valueOf(stgEntity.getOutletId());

		       if (foreignOutletId != null){
		    	   
		    	   // Check if the Invoice has been marked as incomplete
		    	   if(foreignOutletId.equals(Long.valueOf(-1))){
		    		  try{ //Looking for the outlet to complete the staging entity
		    			  foreignOutletId =  getForeignOutletIdForRegister(stgEntity.getStoreId(), stgEntity.getRegisterId());
		    			  stgEntity.setOutletId(foreignOutletId);
		    		  }catch(EmptyResultDataAccessException e){
		    			  // If an empty result is returned the outlet has not yet been stored 
		    			  return null;
		    		  }
		    	   }
		    	   
		    	   final OutletEntity outlet = outletDao.findByStoreAndForeignId(store.getId(), foreignOutletId);
		       
		    	   if (outlet != null && newInvoice != null){
		    		   newInvoice.setOutlet(outlet);
		    		   newInvoice.setLsOutletId(foreignOutletId);
		    	   }
		       }
       	   }else{
        	   LOGGER.warn("Outlet not found for invoice: "+newInvoice.getId());
           }
           
	       // Recovering register
           if(stgEntity.getRegisterId()!= null){
	           final Long foreignRegisterId = Long.valueOf(stgEntity.getRegisterId());
		       
		       if (foreignRegisterId != null){
		    	   final RegisterEntity register = registerDao.findByStoreAndForeignId(store.getId(), foreignRegisterId);
		       
		    	   if (register != null && newInvoice != null){
		    		   newInvoice.setRegister(register);
		    		   newInvoice.setLsRegisterId(foreignRegisterId);
		    	   }
		       }
           }else{
        	   LOGGER.warn("Register not found for invoice: "+newInvoice.getId());
           }
           
           // performs mapping between staging invoice and destination invoice object
           dozerMapper.map(stgEntity, newInvoice);
           
           // sets the reference to the store
           newInvoice.setStore(store);   
           
           // OK, store exists, lets check if legacy customer exits
           final CustomerEntity customer = findCustomerForInvoice(stgEntity, store);
           InvoiceEntityUtil.copyCustomerFieldsToInvoice(newInvoice, customer);
           
           /**
            * Retail Pro invoices might have invalid total values, which
            * need to be adjusted based on their receipt types
            */
           if(newInvoice.getTotal() != null){
               if(StringUtils.hasLength(stgEntity.getReceiptType())){
                   newInvoice.setTotal(getTotalAdjustedBasedOnReceiptType(newInvoice.getTotal(), stgEntity.getReceiptType()));
               }
           }
           
           switch(getFilterResult(stgEntity)){
               case APPROVED:
                   // Steve: "It's a legacy thing from lightspeed imports. It should always be true"
                   //
                   // But as Swarm's PHP code suggests, we will fill out the completed accordingly
                   // to the completed field of Merchant OS. In order to achive this, we will 
                   // only force the completed field to be true, if it's null.
                   if(newInvoice.getCompleted() == null){
                       newInvoice.setCompleted(Boolean.TRUE);
                   }
                   break;
               case MOVABLE_WITH_FLAG:
                   // Invoices with completed set to FALSE do not appear on the Swarm-Site's interface,
                   // but by allowing them to pass throw, it will reduce the logging entries and the 
                   // number invoice lines left in the staging area      
                   if(newInvoice.getCompleted() == null){
                       newInvoice.setCompleted(Boolean.FALSE);
                   }
                   break;
                   
               case RETAINABLE:
                   // They stay in the staging tables
                   return null;
               
           }
           
           if(StringUtils.hasLength(stgEntity.getLinesProcessed())){
               newInvoice.setLinesProcessed(!"0".equals(stgEntity.getLinesProcessed()));
           } else {
               // we don't use this fields, its legacy, so let it be true if not specified
               newInvoice.setLinesProcessed(Boolean.TRUE);
           }
           
           timeZoneService.correctInvoiceTs(store, newInvoice, stgEntity);
       }
       return newInvoice;
   }
   
   /**
    * Returns the result of applying all the filters
    * 
    * @param stgEntity
    * @return
    */
   private StagingFilterValue getFilterResult(InvoiceStage stgEntity){
       List<StagingFilterValue> results = new ArrayList<StagingFilterValue>();
       for(InvoiceStagingFilter filter : invoiceStagingFilters){
           results.add(filter.approve(stgEntity));
       }
       return StagingFilterValue.getMostSevere(results);
   }
   
   /**
    * Finds customer in legacy tables or creates a dummy customer
    * 
    * @param stgEntity
    * @param store
    * @return Found customer, or <i>null</i> if none found
    */
   private CustomerEntity findCustomerForInvoice(InvoiceStage stgEntity, StoreEntity store){

       // Try to find customer in legacy database
       if(!StringUtils.isEmpty(stgEntity.getLsCustomerId())){
           try {
               Long foreignCustomerId = Long.valueOf(stgEntity.getLsCustomerId());
				return customerDao.findByStoreAndForeignId(store.getId(), IdConverter.positiveCustomerId(foreignCustomerId));
           } catch(NumberFormatException e){
               LOGGER.debug("Failed to convert lsCustomerId {} to integer" , stgEntity.getLsCustomerId(), e);
           }
       }
       
       return null;
   }
   
   /**
    * Adjusts the total of invoice based on its receipt type,
    * so return sales are forced to be negative. 
    * 
    * @param total Total of the invoice
    * @param receiptType Receipt type of the invoice
    * @return total, or (-1) total based on the receiptType
    */
   private BigDecimal getTotalAdjustedBasedOnReceiptType(BigDecimal total, String receiptType){
      if(total == null){
          throw new IllegalArgumentException("Total is null");
      }
       
      try {
          int intValue = Integer.parseInt(receiptType);
          
          // Sales are untouched
          if(intValue == RpReceiptType.SALES.getLsReceiptType()){
              return total;
              
          // Returns are ALWAYS negative
          } else if(intValue == RpReceiptType.RETURN.getLsReceiptType()){
              // if total is positive
              if(total.signum() == 1){
                  return total.negate();
              } else {
                  return total;
              }
              
          // Others are untouched
          } else {
              return total;
          }
      } catch (NumberFormatException e){
          LOGGER.warn("Failed to parse ReceiptType: {} as integer", receiptType);
          return total;
      }
   }

    /**
     * @param dao the dao to set
     */
    public void setInvoiceDao(InvoiceDao dao) {
        this.dao = dao;
    }
    
    
    /**
     * @param invoiceStagingService the invoiceStagingService to set
     */
    public void setInvoiceStagingService(InvoiceStagingService invoiceStagingService) {
        this.invoiceStagingService = invoiceStagingService;
    }
    
    
    /**
     * @param customerDao the customerDao to set
     */
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    
    /**
     * @param dozerMapper the dozerMapper to set
     */
    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
    
    
    /**
     * @param timeZoneService the timeZoneService to set
     */
    public void setTimeZoneService(TimeZoneService timeZoneService) {
        this.timeZoneService = timeZoneService;
    }
    
    
    /**
     * @param invoiceStagingFilters the invoiceStagingFilters to set
     */
    public void setInvoiceStagingFilters(List<InvoiceStagingFilter> invoiceStagingFilters) {
        this.invoiceStagingFilters = invoiceStagingFilters;
    }
    
    private static final String SELECT_OUTLET_FOR_REGISTER = 
			"SELECT ls_outlet_id FROM registers "
			+ "WHERE store_id = ? "
			+ "AND ls_register_id = ?";
	
	/**
	 * This function returns the foreign Id of the outlet associated to a concrete register
	 */
	public Long getForeignOutletIdForRegister(Long storeId, Long registerId){
		
		Object[] param = new Object[]{ storeId, registerId };
		
		return jdbcTemplate.queryForObject(SELECT_OUTLET_FOR_REGISTER, param, Long.class);
	}
}
