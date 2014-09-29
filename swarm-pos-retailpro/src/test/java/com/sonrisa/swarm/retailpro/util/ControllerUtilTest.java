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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for this class: {@link ControllerUtil}.
 *
 * @author joe
 */
public class ControllerUtilTest {
    

    @Test
    public void testGetSourceId() {
        final String swarmId = "mySwarmId";
        final String posSoftware = "myPosSoftware";
        assertEquals(swarmId+"-"+posSoftware, ControllerUtil.getSourceId(swarmId, posSoftware));
    }
    
    /**
     * Expected behavior: Without swarmId a default one should be used.
     */
    @Test
    public void testGetSourceIdWithoutSwarmId() {
        final String posSoftware = "myPosSoftware";
        assertEquals("unknown-"+posSoftware, ControllerUtil.getSourceId(null, posSoftware));
        assertEquals("unknown-"+posSoftware, ControllerUtil.getSourceId("", posSoftware));
    }
    
    /**
     * Expected behavior: Without posSoftware name only the swarmId should be used.
     */
    @Test
    public void testGetSourceIdWithoutPosSoftware() {
        final String swarmId = "someSwarmId";
        assertEquals("someSwarmId", ControllerUtil.getSourceId(swarmId, ""));
        assertEquals("someSwarmId", ControllerUtil.getSourceId(swarmId, null));
    }
}