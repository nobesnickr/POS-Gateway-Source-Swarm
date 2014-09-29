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
package com.sonrisa.swarm.revel;

import org.eclipse.persistence.jpa.jpql.Assert.AssertException;
import org.junit.Test;
import static org.junit.Assert.*;

import com.sonrisa.swarm.revel.exception.RevelResourcePathFormatException;
import com.sonrisa.swarm.revel.util.RevelResourcePathConverter;

/**
 * Test class for {@link RevelResourcePathConverter}
 *
 */
public class RevelResourcePathConverterTest {
    
    final static private String BASE_PATH = "resources/Order/";
    final static private Long PATH_VALUE = 1L;

    /**
     * Test case:
     *  Resource path is ending with slash '/'
     *  or not
     *  
     * Expected:
     *  No error, resource path is parsed
     *  
     * @throws RevelResourcePathFormatException 
     */
    @Test
    public void testConverting() throws RevelResourcePathFormatException{
        
        final Long resultWithSlash = RevelResourcePathConverter.resourcePathToLong(BASE_PATH, BASE_PATH + PATH_VALUE + "/");
        final Long resultWithNone = RevelResourcePathConverter.resourcePathToLong(BASE_PATH, BASE_PATH + PATH_VALUE );
        
        assertEquals(PATH_VALUE, resultWithSlash);
        assertEquals(PATH_VALUE, resultWithNone);
        
    }
    
}
