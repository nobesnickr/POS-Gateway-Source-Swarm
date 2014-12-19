/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.model;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author golyo
 */
public class AbsztractMuveletekEredmenyTest {

    private AbsztraktMuveletEredmenye<String> muveletek;

    @Test
    public void test() {
        muveletek = new AbsztraktMuveletEredmenye<String>() {
        };
        muveletek.setStatusz(MuveletStatusz.SIKERES);
        muveletek.setCelpont("celpont");
        muveletek.addUzenet(UzenetTipus.INFORMACIO, "teszt");
        Assert.assertEquals(muveletek.getResourceUzenetek().size(), 1);
        Assert.assertEquals(muveletek.getResourceUzenetek().get(UzenetTipus.INFORMACIO).size(), 1);
    }
}