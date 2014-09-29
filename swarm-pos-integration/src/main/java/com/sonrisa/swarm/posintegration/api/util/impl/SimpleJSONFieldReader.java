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
package com.sonrisa.swarm.posintegration.api.util.impl;

import com.sonrisa.swarm.posintegration.api.util.JSONFieldReader;

/**
 * Implementation which annotates the raw content
 */
public class SimpleJSONFieldReader implements JSONFieldReader {
    
    /**
     * Label used for annotating
     */
    private String label;

    public SimpleJSONFieldReader(String label) {
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMeta(String raw) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label).append(": ").append(raw);
        return stringBuilder.toString();
    }
}
