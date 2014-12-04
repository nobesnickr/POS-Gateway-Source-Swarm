package com.sonrisa.swarm.staging.converter.impl;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.legacy.dao.OutletDao;
import com.sonrisa.swarm.legacy.util.IdConverter;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.OutletEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.OutletStage;
import com.sonrisa.swarm.staging.converter.OutletStagingConverter;
import com.sonrisa.swarm.staging.service.OutletStagingService;

@Service
public class OutletStagingConverterImpl extends BaseStagingConverterImpl<OutletStage, OutletEntity> implements OutletStagingConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutletStagingConverterImpl.class);
    
    @Autowired
    private OutletDao dao;
    
    @Autowired
    private OutletStagingService outletStagingService;
    
	@Override
	public StageAndLegacyHolder<OutletStage, OutletEntity> convert(OutletStage stageEntity) {
		OutletEntity outlet = null;

        final StoreEntity store = outletStagingService.findStore(stageEntity);
        if (store == null) {
            LOGGER.debug("Staging outlet can not be saved because its store does not exists: " + stageEntity);
        } else {
            try {
                final Long foreignOutletId = stageEntity.getOutletId();
                outlet = findOrCreateOutlet(store.getId(), foreignOutletId);
            } catch (NumberFormatException e){
                final String errorMsg = "Illegal foreign id: " + stageEntity.getOutletId();
                LOGGER.debug("Failed to convert OutletStage to OutletEntity because: {}", errorMsg, e);
                return new StageAndLegacyHolder<OutletStage, OutletEntity>(stageEntity, errorMsg);
            }

            // performs mapping between staging outlet and destination outlet object
            copyStgOutlet(stageEntity, outlet);
            outlet.setOutletId(IdConverter.positiveCustomerId(stageEntity.getOutletId()));
            outlet.setStore(store);   // sets the reference to the store
        }
               
        return  new StageAndLegacyHolder<OutletStage, OutletEntity>(outlet, stageEntity);
	}

	public void setDao(OutletDao dao) {
		this.dao = dao;
	}

	public void setOutletStagingService(OutletStagingService outletStagingService) {
		this.outletStagingService = outletStagingService;
	}

	private void copyStgOutlet(OutletStage stageEntity, OutletEntity outlet) {
		dozerMapper.map(stageEntity, outlet);
	}
	
	public void setDozerMapper(DozerBeanMapper dozerMapper) {
		this.dozerMapper = dozerMapper;
	}

	private OutletEntity findOrCreateOutlet(Long id, Long foreignOutletId) {
		OutletEntity outlet = dao.findByStoreAndForeignId(id, IdConverter.positiveCustomerId(foreignOutletId));
        if (outlet == null){
            LOGGER.debug("Staging outlet can not be found in the Data warehouse so a new one will be created. StoreId " 
                    + id + " foreign outletId: " + foreignOutletId);
            outlet = new OutletEntity();                        
        }else{
            LOGGER.debug("Staging outlet has been found in the Data warehouse so it will be updated. StoreId " 
                    + id + " foreign outletId: " + foreignOutletId);
        }
        return outlet;
	}

}
