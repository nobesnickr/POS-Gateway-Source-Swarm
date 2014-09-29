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
package com.sonrisa.swarm.posintegration.extractor;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

/**
 * A path inside an {@link ExternalDTO} leads to an other {@link ExternalDTO} or nothing.
 * 
 * Used by {@link ExternalDTO#getNestedItem(String...)}
 * 
 * @author Barnabas
 */
public class ExternalDTOPath {

    /**
     * Fields leading to the destination field
     */
    private String[] fields;

    /**
     * @param fields Fields leading to the destination field
     */
    public ExternalDTOPath(String... fields) {
        super();
        this.fields = fields;
    }
    
    /**
     * @return DTO path leading to root
     */
    public static ExternalDTOPath getRootPath(){
        return new ExternalDTOPath();
    }

    public void setFields(String[] fields) {
        if(fields == null){
            throw new IllegalArgumentException("fields can't be null");
        }
        this.fields = Arrays.copyOf(fields, fields.length);
    }
    
    public String[] getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return StringUtils.join(fields,"/");
    }
}
