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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.util.ProductEntityUtil;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.CategoryEntity;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.staging.converter.ProductStagingConverter;
import com.sonrisa.swarm.staging.service.ProductStagingService;


/**
 * Implementation of the {@link ProductStagingConverter} class.
 * @author sonrisa
 *
 */
@Service
public class ProductStagingConverterImpl  extends BaseStagingConverterImpl<ProductStage, ProductEntity> implements ProductStagingConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStagingConverterImpl.class);

    /**
     * DAO of products in the data warehouse (aka legacy DB).
     */
    @Autowired
    private ProductDao dao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ManufacturerDao manufacturerDao;
        
    @Autowired
    private ProductStagingService productStagingService;
    
    /**
     * {@inheritDoc }
     * 
     * @param stgProd
     * @return 
     */
    @Override
    public StageAndLegacyHolder<ProductStage, ProductEntity> convert(ProductStage stgProd) {
        ProductEntity prod = null;

        final StoreEntity store = productStagingService.findStore(stgProd);
        if (store == null) {
            LOGGER.debug("Staging product can not be saved because his store does not exists: " + stgProd);
        } else {
            try {
                final Long foreignProductId = Long.parseLong(stgProd.getLsProductId());
                prod = findOrCreateProduct(store.getId(), foreignProductId);
            } catch (NumberFormatException e){
                final String errorMsg = "Illegal foreign id: " + stgProd.getLsProductId();
                LOGGER.debug("Failed to convert ProductStage to ProductEntity because: {}", errorMsg, e);
                return new StageAndLegacyHolder<ProductStage, ProductEntity>(stgProd, errorMsg);
            }

            // performs mapping between staging product and destination product object
            dozerMapper.map(stgProd, prod);
            prod.setStore(store);   // sets the reference to the store
            
            CategoryEntity category = findCategory(store.getId(), stgProd);
            if(category != null){
                ProductEntityUtil.copyCategoryFieldsOnProduct(prod, category);
            }
           
            ManufacturerEntity manufacturer = findManufacturer(store.getId(), stgProd);
            if(manufacturer != null){
                ProductEntityUtil.copyManufacturerFieldsOnProduct(prod, manufacturer);
            }
        }
        return new StageAndLegacyHolder<ProductStage, ProductEntity>(prod, stgProd);
    }
    
    /**
     * Find a product by his storeId and foreign Id. (The foreignId is the ID
     * that identifies the product in the source system.)
     *
     * @param storeId
     * @param foreignId     
     * @return
     */
    private ProductEntity findOrCreateProduct(final Long storeId, final Long foreignId) {
        ProductEntity prod = dao.findByStoreAndForeignId(storeId, foreignId);
        if (prod == null) {
            LOGGER.debug("Staging product can not be found in the Data warehouse so a new one will be created. StoreId "
                    + storeId + " foreignId: " + foreignId);
            prod = new ProductEntity();
        } else {
            LOGGER.debug("Staging product has been found in the Data warehouse so it will be updated. StoreId "
                    + storeId + " foreignId: " + foreignId);
        }
        return prod;
    }
    
    private CategoryEntity findCategory(Long storeId, ProductStage stgProd){
        // If referencing category
        if(StringUtils.hasLength(stgProd.getLsCategoryId())){
            try {
                Long lsCategoryId = Long.parseLong(stgProd.getLsCategoryId());
                return categoryDao.findByStoreAndForeignId(storeId, lsCategoryId);
            } catch (NumberFormatException e){
                LOGGER.warn("Invalid referenced category: {} for {}", stgProd.getLsCategoryId(), stgProd, e);
                // fall through and returning null
            }
        }
        return null;
    }
    
    private ManufacturerEntity findManufacturer(Long storeId, ProductStage stgProd){
        // If referencing category
        if(StringUtils.hasLength(stgProd.getLsManufacturerId())){
            try {
                Long lsManufacturerId = Long.parseLong(stgProd.getLsManufacturerId());
                return manufacturerDao.findByStoreAndForeignId(storeId, lsManufacturerId);
            } catch (NumberFormatException e){
                LOGGER.warn("Invalid referenced manufacturer: {} for {}", stgProd.getLsCategoryId(), stgProd, e);
                // fall through and returning null
            }
        }
        return null;
    }
}
