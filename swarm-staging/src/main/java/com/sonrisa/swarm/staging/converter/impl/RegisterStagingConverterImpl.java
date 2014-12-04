package com.sonrisa.swarm.staging.converter.impl;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.OutletDao;
import com.sonrisa.swarm.legacy.dao.RegisterDao;
import com.sonrisa.swarm.legacy.util.IdConverter;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.OutletEntity;
import com.sonrisa.swarm.model.legacy.RegisterEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.RegisterStage;
import com.sonrisa.swarm.staging.converter.RegisterStagingConverter;
import com.sonrisa.swarm.staging.service.RegisterStagingService;


@Service
public class RegisterStagingConverterImpl  extends BaseStagingConverterImpl<RegisterStage, RegisterEntity> implements RegisterStagingConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterStagingConverterImpl.class);
    
    @Autowired
    private RegisterDao dao;
    
    @Autowired
    private OutletDao outletDao;
    
    @Autowired
    private RegisterStagingService registerStagingService;
	
    @Override
	public StageAndLegacyHolder<RegisterStage, RegisterEntity> convert(RegisterStage stageEntity) {
    	RegisterEntity register = null;

        final StoreEntity store = registerStagingService.findStore(stageEntity);
        if (store == null) {
            LOGGER.debug("Staging register can not be saved because its store does not exists: " + stageEntity);
        } else {


            // check whether its outlet exists
	        final Long foreignOutletId = Long.valueOf(stageEntity.getOutletId());
	        final OutletEntity outlet = outletDao.findByStoreAndForeignId(store.getId(), foreignOutletId);           
	        if (outlet == null) {
	            LOGGER.debug("Staging register can not be saved because its outlet (foreignId:"
	                    + foreignOutletId + ") does not exists: " + stageEntity);
	            return null;
	        }                 
	        // OK, outlet exists
      
	        try {
                final Long foreignRegisterId = stageEntity.getRegisterId();
                register = findOrCreateRegister(store.getId(), foreignRegisterId, outlet);
            } catch (NumberFormatException e){
                final String errorMsg = "Illegal foreign id: " + stageEntity.getRegisterId();
                LOGGER.debug("Failed to convert RegisterStage to RegisterEntity because: {}", errorMsg, e);
                return new StageAndLegacyHolder<RegisterStage, RegisterEntity>(stageEntity, errorMsg);
            }            
            
            // performs mapping between staging register and destination register object
            copyStgRegister(stageEntity, register);
            register.setRegisterId(IdConverter.positiveCustomerId(stageEntity.getRegisterId()));
            register.setStore(store);   // sets the reference to the store
        }
               
        return  new StageAndLegacyHolder<RegisterStage, RegisterEntity>(register, stageEntity);
	}

	public void setDao(RegisterDao dao) {
		this.dao = dao;
	}

	public void setRegisterStagingService(RegisterStagingService registerStagingService) {
		this.registerStagingService = registerStagingService;
	}

	private void copyStgRegister(RegisterStage stageEntity, RegisterEntity register) {
		dozerMapper.map(stageEntity, register);
	}
	
	public void setDozerMapper(DozerBeanMapper dozerMapper) {
		this.dozerMapper = dozerMapper;
	}

	private RegisterEntity findOrCreateRegister(Long id, Long foreignRegisterId, OutletEntity outlet) {
		RegisterEntity register = dao.findByStoreAndForeignId(id, IdConverter.positiveCustomerId(foreignRegisterId));
        if (register == null){
            LOGGER.debug("Staging register can not be found in the Data warehouse so a new one will be created. StoreId " 
                    + id + " foreign registerId: " + foreignRegisterId);
            register = new RegisterEntity();
            register.setOutlet(outlet);
        }else{
            LOGGER.debug("Staging register has been found in the Data warehouse so it will be updated. StoreId " 
                    + id + " foreign registerId: " + foreignRegisterId);
        }
        return register;
	}

}
