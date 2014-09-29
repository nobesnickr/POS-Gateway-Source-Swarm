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
package com.sonrisa.swarm.retailpro.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Commom utility methods for controller classes.
 *
 * @author joe
 */
public abstract class ControllerUtil {

    /**
     * Utility class can not be instantiated.
     * 
     */
    private ControllerUtil() {}
    
    /**
     * This method appends a string that identifies the
     * client and his RP version based on the given swarmId and pos software name.
     * 
     * @param swarmId
     * @param posSoftware
     * @return 
     */
    public static String getSourceId(final String swarmId, final String posSoftware) {
        final StringBuilder sourceString = new StringBuilder();
        
        sourceString.append(StringUtils.isEmpty(swarmId) ? "unknown" : swarmId);        
        if(!StringUtils.isEmpty(posSoftware)){
            sourceString.append("-");
            sourceString.append(posSoftware);
        }
        
        return sourceString.toString();
    }
    
    
    
}
