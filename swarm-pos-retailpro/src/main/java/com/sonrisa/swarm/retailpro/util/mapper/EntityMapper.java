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
package com.sonrisa.swarm.retailpro.util.mapper;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.common.exception.ItemMappingException;
import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.model.staging.retailpro.converter.RetailProConverter;
import com.sonrisa.swarm.retailpro.model.RpClientEntity;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.retailpro.rest.model.JsonStore;
import com.sonrisa.swarm.retailpro.rest.model.RpClientJson;

/**
 * Entity mapper responsible for transforming the JSON
 * objects to swarm entities.
 *
 * @author joe
 */
@Component
public class EntityMapper implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityMapper.class);
     
    /** Spring application context. We can get converter beans from it. */
    private ApplicationContext appContext;
    
    /**
     * Converts the JSON map to an entity object.
     *
     * @param <T> type of the target entity
     * @param map json map
     * @param clazz type of the target entity
     * @return
     */
    public <T extends BaseStageEntity> T convertToStageEntity(Map<String, Object> map, Class<T> clazz) {
        return convertToSwarmEntity(map, clazz, null);
    }

    /**
     * Converts {@link JsonStore} to {@link RpStoreEntity} using the {@link RetailProAttr} annotation
     * @param entity Entity to be used for not mapped fields
     * @param jsonStore
     * @return
     */
    public RpStoreEntity copyToRpStore(final RpStoreEntity entity, final JsonStore jsonStore){
        ObjectMapper mapper = new ObjectMapper();
        return convertToSwarmEntity(mapper.convertValue(jsonStore, Map.class), RpStoreEntity.class, entity);
    }
    
    /**
     * Converts {@link JsonStore} to {@link RpStoreEntity} using the {@link RetailProAttr} annotation
     * @param jsonStore
     * @return
     */
    public RpStoreEntity copyToRpStore(final JsonStore jsonStore){
        ObjectMapper mapper = new ObjectMapper();
        return convertToSwarmEntity(mapper.convertValue(jsonStore, Map.class), RpStoreEntity.class, null);
    }
    
    public void copyJsonToEntity(RpClientJson json, RpClientEntity entity){
        // TODO boilerplate code --> need more elegant solution
        entity.setComments(json.getComments());
        entity.setComponentId(json.getComponentId());
        entity.setComponentType(json.getComponentType());
        entity.setInstallDate(json.getInstallDate());
        entity.setRpVersion(json.getVersion());        
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
    
    
    // ------------------------------------------------------------------------
    // ~ Private methods
    // ------------------------------------------------------------------------
    private <T extends BaseSwarmEntity> T convertToSwarmEntity(Map<String, Object> map, Class<T> clazz, T entity) {
        try {
            if(entity == null){
                entity = clazz.newInstance();
            }
            
            // iterates over the declared fields
            for (Field fieldOnEntity : clazz.getDeclaredFields()) {

                // get the annotation from the field
                RetailProAttr rProAnnotation = fieldOnEntity.getAnnotation(RetailProAttr.class);
                if (rProAnnotation != null) {
                    Object jsonValue = null;
                    
                   // get a reference to the converter which is defined on the annotation
                    RetailProConverter converter = getConverter(rProAnnotation, clazz, fieldOnEntity);
                    if (converter != null) {
                        jsonValue = converter.getValueFromMap(map, rProAnnotation);
                    }
                   
                    final String keyInJsonMap = rProAnnotation.value();     
                    setValue(jsonValue, fieldOnEntity, entity, keyInJsonMap, clazz);        
                } else {
                    // annotation is missing from the field
                    LOGGER.debug("This field does not have retail pro annotation: " + fieldOnEntity.getName() + " class: " + clazz.getSimpleName());
                }
            }
            
            return entity;
        } catch (InstantiationException ex) {
            throw new ItemMappingException("An exception occured during the jsonMap and entity transformation. Item class: " + clazz.getSimpleName(), ex);
        } catch (IllegalAccessException ex) {
            throw new ItemMappingException("An exception occured during the jsonMap and entity transformation. Item class: " + clazz.getSimpleName(), ex);
        }
    }
    
    /**
     * Sets the given value to the entity's given field.
     * 
     * @param <T>
     * @param jsonValue
     * @param fieldOnEntity
     * @param entity
     * @param keyInJsonMap
     * @param clazz
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException 
     */
    private static <T extends BaseSwarmEntity> void setValue(final Object jsonValue, Field fieldOnEntity, T entity, final String keyInJsonMap, Class<T> clazz) throws IllegalAccessException, IllegalArgumentException, SecurityException {
        if (jsonValue != null) {
            // there is no value for this field in the JSON map
            fieldOnEntity.setAccessible(true);
            try{
                fieldOnEntity.set(entity, jsonValue);
            }catch (IllegalArgumentException ex){
                LOGGER.warn("The [" + jsonValue + "] value can not be set to this field: " + fieldOnEntity.getName() + " class: " + clazz.getSimpleName());
            }
        } else {
            LOGGER.debug("The JSON map does not contain value for this key: " + keyInJsonMap + " class: " + clazz.getSimpleName());
        }
    }

    /**
     * Returns a converter object for the given field.
     * The converter object can be used in order to get the appropriate value from the JSON map
     * for this field.
     * 
     * @param rProAnnotation
     * @param clazz
     * @param field
     * @return
     * @throws BeansException 
     */
    private RetailProConverter getConverter(RetailProAttr rProAnnotation, Class clazz, Field field) throws BeansException {
        RetailProConverter converter = null;
        
        Class<? extends RetailProConverter> customConverter = rProAnnotation.converter();
        Map<String, ? extends RetailProConverter> converters = appContext.getBeansOfType(customConverter);
        if (converters != null && !converters.isEmpty()){
            Collection<? extends RetailProConverter> converterBeans = converters.values();
            
            if (LOGGER.isDebugEnabled() && converterBeans.size() > 1){
                LOGGER.debug("More than one converter implementation has been found, the first will be used. ConverterClass: " 
                        + customConverter.getSimpleName() + " entityClass: " + clazz.getSimpleName() + " field: " + field.getName());
            }
            
            converter = converterBeans.iterator().next();
            
        }else{
            LOGGER.debug("Custom converter has not been found. ConverterClass: " + customConverter.getSimpleName() 
                    + " entityClass: " + clazz.getSimpleName() + " field: " + field.getName());
        }
        
        return converter;
    }
}