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

package com.sonrisa.swarm.legacy.util;

import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.legacy.CategoryEntity;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;

/**
 * Helper class for managing {@link ProductEntity}
 * 
 * @author Barnabas Szirmay <szirmayb@sonrisa.hu>
 */
public class ProductEntityUtil {
    
    /**
     * Copies category's name onto product
     * @param product
     * @param source
     */
    public static void copyCategoryFieldsOnProduct(ProductEntity product, final CategoryEntity source){
        if(product == null){
            throw new IllegalArgumentException("product is null");
        }
        
        if(source == null){
            // Do nothing
            return;
        }
        
        if(StringUtils.isEmpty(product.getCategory())){
            product.setCategory(source.getName());
        }
    }
    
    /**
     * Copies manufacturer's name onto product
     * @param product
     * @param source
     */
    public static void copyManufacturerFieldsOnProduct(ProductEntity product, final ManufacturerEntity source){
        if(product == null){
            throw new IllegalArgumentException("product is null");
        }
        
        if(source == null){
            // Do nothing
            return;
        }
        
        if(StringUtils.isEmpty(product.getManufacturer())){
            product.setManufacturer(source.getManufacturerName());
        }
    }

}
