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
package com.sonrisa.swarm.revel.util;

import com.sonrisa.swarm.revel.exception.RevelResourcePathFormatException;


/**
 * Class converting RevelResource paths (e.g. /resources/PosStation/2/) to foreign ids (2)
 */
public class RevelResourcePathConverter {
    
    /**
     * Converts Revel's resource path (e.g. /resources/PosStation/2/) to foreign ids (2)
     * @param expectedPrefix E.g. /resources/PosStation/
     * @param resourcePath E.g. /resources/PosStation/2/
     * @return E.g. 2
     * @throws RevelResourcePathFormatException
     */
    public static Long resourcePathToLong(final String expectedPrefix, final String resourcePath) throws RevelResourcePathFormatException{
        
        final String endOfResourcePath = resourcePath.substring(resourcePath.length()-1, resourcePath.length());
        final String pathWithCorrectEnd = endOfResourcePath.equals("/") ? resourcePath.substring(0,resourcePath.length()-1) : resourcePath;
        
        if(!pathWithCorrectEnd.startsWith(expectedPrefix)){
            throw new RevelResourcePathFormatException(resourcePath, expectedPrefix);
        }
        
        try {
            return Long.parseLong(pathWithCorrectEnd.substring(expectedPrefix.length(), pathWithCorrectEnd.length()));
        } catch (NumberFormatException e){
            throw new RevelResourcePathFormatException(e);
        }
    }
    
    
}
