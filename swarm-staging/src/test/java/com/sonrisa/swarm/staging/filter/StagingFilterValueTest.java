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
package com.sonrisa.swarm.staging.filter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test class for the {@link StagingFilterValue#getMostSevere(java.util.List) method
 */
public class StagingFilterValueTest {
    
    /**
     * Test case: 
     *  There are different severity levels of the FilterValues
     *  
     * Expected:
     *  APPROVED, MOVABLE, RETAINABLE, UNPROCESSABLE is the priority
     */
    @Test
    public void testGetMostSevereFilter(){
        
        final List<StagingFilterValue> approvedTest = Arrays.asList(StagingFilterValue.APPROVED, StagingFilterValue.APPROVED);
        assertEquals(StagingFilterValue.APPROVED, StagingFilterValue.getMostSevere(approvedTest));
        
        final List<StagingFilterValue> flagTest = Arrays.asList(StagingFilterValue.APPROVED, StagingFilterValue.MOVABLE_WITH_FLAG, StagingFilterValue.APPROVED);
        assertEquals(StagingFilterValue.MOVABLE_WITH_FLAG, StagingFilterValue.getMostSevere(flagTest));
        
        final List<StagingFilterValue> retainableTest = Arrays.asList(StagingFilterValue.RETAINABLE, StagingFilterValue.MOVABLE_WITH_FLAG);
        assertEquals(StagingFilterValue.RETAINABLE, StagingFilterValue.getMostSevere(retainableTest));
    }
}
