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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.legacy.model.EntityValidationResult;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.BaseLegacyEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.staging.converter.BaseStagingConverter;

/**
 * Base class for implementations of the {@link BaseStagingConverter} with some common 
 * methods.
 * 
 * @author Barnabas
 */
public abstract class BaseStagingConverterImpl<U extends BaseStageEntity, T extends BaseLegacyEntity> implements BaseStagingConverter<U, T> {

    /**
     * Utility object to map staging and legacy entities.
     */
    @Autowired
    protected DozerBeanMapper dozerMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityValidationResult validateEntity(StageAndLegacyHolder<U, T> holder) {
        EntityValidationResult result = EntityValidationResult.success();

        T legacyEntity = holder.getLegacyEntity();

        // If not null (null means skipping)
        if (legacyEntity != null) {

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<T>> constraintViolations = validator.validate(legacyEntity);

            // If any violation
            if (!constraintViolations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder();
                for (ConstraintViolation<T> violation : constraintViolations) {
                    if (errorMessage.length() != 0) {
                        errorMessage.append(" ");
                    }
                    errorMessage.append(violation.getMessage());
                }
                result = EntityValidationResult.failure(errorMessage.toString());
            }
        }

        return result;
    }
}
