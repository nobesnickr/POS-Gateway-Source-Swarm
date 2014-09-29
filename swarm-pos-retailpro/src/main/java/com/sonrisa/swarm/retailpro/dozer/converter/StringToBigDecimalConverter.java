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
package com.sonrisa.swarm.retailpro.dozer.converter;

import java.math.BigDecimal;

import org.dozer.ConfigurableCustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.StringUtils;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

/**
 * Dozer converter that converts string values to BigDecimal.
 *
 * @author joe
 */
public class StringToBigDecimalConverter  implements ConfigurableCustomConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringToBigDecimalConverter.class);
    
    /** Scale of the BigDecimal value */
    private int scale;
    
    @Override
    public void setParameter(String parameter) {
        if (parameter != null){
            scale = Integer.parseInt(parameter);
        }else{
            scale = StageInsertableType.doublePrecision;
        }
    }
   
    
    @Override
    public Object convert(Object existingDestinationFieldValue, 
        Object sourceFieldValue, 
        Class<?> destinationClass, 
        Class<?> sourceClass) {
        
        if(sourceFieldValue == null){
            return null;
        }
        
        String sourceFieldText = (String)sourceFieldValue;
        if(StringUtils.isNullOrEmpty(sourceFieldText)){
            return null;
        }
        
        if(sourceFieldText.indexOf(',') >= 0){
            LOGGER.debug("Attempting to convert number {} with comma as decimal point", sourceFieldText);
            sourceFieldText = sourceFieldText.replaceAll(",", ".");
        }

        BigDecimal value = BigDecimal.valueOf(Double.parseDouble(sourceFieldText));
        
        return value;
    }
}
