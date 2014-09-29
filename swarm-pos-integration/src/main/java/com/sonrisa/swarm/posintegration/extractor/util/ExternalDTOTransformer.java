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
package com.sonrisa.swarm.posintegration.extractor.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An ExternalDTOTrasformer provides the functionality to an extractor
 * to use an annotation field, and create DataStoreTransferable DTO
 * objects from an ExternalDTO entity;
 */
@Component
public class ExternalDTOTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalDTOTransformer.class);
    
    /**
     * Transform many ExternalDTO entities into a List of 
     * DataStoreTransferable internal DTO objects. 
     * 
     * @param externalItems Many external items, e.g. JSON entities
     * @param clazz Class of the resulting DTO, e.g. ErplyInvoiceDTO
     * @return List of the transformed DTOs
     * @throws ExternalExtractorException
     */
    public <K extends DWTransferable> List<K> transformManyDTO(
            Iterable<ExternalDTO> externalItems, Class<K> clazz) throws  ExternalExtractorException{
        
        List<K> retval = new ArrayList<K>();
        for(ExternalDTO externalDTO : externalItems){
            retval.add(transformDTO(externalDTO, clazz));
        }
        return retval;        
    }
    
    /**
     * Transforms an ExternalDTO into a StageInsertable/DataStoreTransferable DTO,
     * if class is correctly annotated with ExternalField annotation.
     * 
     * For example: transformDTO(jsonDTO, ErplyCustomerDTO.class, ExternalField.class)
     * 
     * @param externalDTO ExternalDTO to read the field values form
     * @param clazz Class of the internal DTO to be transformed into
     * @return The mapped internal DTO 
     * @throws ExternalExtractorException If reading from externalDTO fails
     */
    public <K extends DWTransferable> K transformDTO(
            ExternalDTO externalDTO, Class<K> clazz) throws  ExternalExtractorException {
        
        // Initialize an instance of the InternalDTO
        K internalDTO;
        try {
            internalDTO = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        
        transformDTO(internalDTO, externalDTO, clazz);
        
        return internalDTO;
    }
        
    /**
     * Transforms an ExternalDTO into a StageInsertable/DataStoreTransferable DTO,
     * if class is correctly annotated with ExternalField annotation.
     * 
     * For example: transformDTO(customer, jsonDTO, ErplyCustomerDTO.class, ExternalField.class)
     * 
     * @param internalDTO Result of the transformation
     * @param externalDTO ExternalDTO to read the field values form
     * @param clazz Class of the internal DTO to be transformed into
     * @throws ExternalExtractorException If reading from externalDTO fails
     */
    public <K extends DWTransferable> void transformDTO(
            K internalDTO, ExternalDTO externalDTO, Class<K> clazz) throws  ExternalExtractorException {
        
        for(Method method : clazz.getMethods()){

            //If method has ExternalField annotation
            if(method.isAnnotationPresent(ExternalField.class)){
                try {
                    if(!method.getName().startsWith("set")){
                        throw new ExternalExtractorException("Only methods with prefix 'set' should be annotated with ExternalField annotation");
                    }
                    
                    ExternalField externalField = method.getAnnotation(ExternalField.class);
                    String key = externalField.value();
                                        
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    
                    // Only functions with one argument should be annotated
                    if(parameterTypes.length != 1){
                        throw new RuntimeException("ExternalField annotation should annotate methods with only one argument for " + externalField.toString());
                    }
                    
                    // If field is missing in the externalDTO, than either skip it
                    // or throw exception if field was marked as required
                    if(!externalDTO.hasKey(key)){
                        if(externalField.required()){
                            throw new ExternalExtractorException("Key not found in external object: " + key, clazz);
                        } else {
                            continue;
                        }
                    }
                    
                    // Depending on the type of the argument
                    // as the appropriate method of the ExternalDTO
                    // interface to retrieve the correct information.
                    Class<?> paramType = parameterTypes[0];
                    if(paramType == long.class || paramType == Long.class){
                        long value = externalDTO.getLong(key);
                        method.invoke(internalDTO, value);
                    } else if (paramType == int.class || paramType == Integer.class){
                        int value = externalDTO.getInt(key);
                        method.invoke(internalDTO, value);
                    } else if(paramType == double.class || paramType == Double.class){
                        double value = externalDTO.getDouble(key);
                        method.invoke(internalDTO, value);
                    } else if(paramType == short.class || paramType == Short.class){
                    	short value = externalDTO.getShort(key);
                        method.invoke(internalDTO, value);
                    } else if(paramType == BigDecimal.class){
                        String value = externalDTO.getText(key);
                        method.invoke(internalDTO, asBigDecimalOrNull(value));
                    } else if(paramType == String.class){
                        String value = externalDTO.getText(key);
                        method.invoke(internalDTO, value);
                    } else if(paramType == ExternalDTO.class){
                        ExternalDTO nestedDocument = externalDTO.getNestedItem(new ExternalDTOPath(key));
                        method.invoke(internalDTO, nestedDocument);
                    } else if(paramType == Iterable.class){
                        Iterable<ExternalDTO> nestedDocument = externalDTO.getNestedItems(key);
                        method.invoke(internalDTO, nestedDocument);
                    } else if(paramType == Timestamp.class){
                        Timestamp iso8061 = externalDTO.getTimeStampISO8061(key);
                        method.invoke(internalDTO, iso8061);
                    } else {
                        throw new ExternalExtractorException("Unsupported method for transforming:" + method.getName());
                    }
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (ReflectiveOperationException e) {
                    LOGGER.debug("Error occured during trasformation into {}", clazz.getSimpleName(), e);
                    
                    // Certain setters might throw ExternalExtractorException
                    if(e.getCause() instanceof ExternalExtractorException){
                        throw (ExternalExtractorException)e.getCause();
                    }
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    /**
     * Attempts to parse value as BigDecimal and returns null if fails
     * @param value
     * @return
     */
    private BigDecimal asBigDecimalOrNull(String value){
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e){
            LOGGER.trace("Failed to transform to BigDecimal: {}", value, e);
            return null;
        }
    }
}
