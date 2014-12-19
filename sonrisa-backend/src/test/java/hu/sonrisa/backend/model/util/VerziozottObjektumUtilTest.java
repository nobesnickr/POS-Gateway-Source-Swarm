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

package hu.sonrisa.backend.model.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import hu.sonrisa.backend.versionedobject.VersionedObjectBase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kelemen
 */
public class VerziozottObjektumUtilTest {

    /**
     * Test of getMegjelenithetoVerzio method, of class VerziozottObjektumUtil.
     */
    @Test
    public void testNull() {
        try{
            VersionedObjectUtil.getMegjelenithetoVerzio(null);
        } catch (Exception ex) {
           assertTrue(ex instanceof NullPointerException);
           return;
        }
        fail("Hiba nem jött NPE!");
    }

    @Test
    public void testGetMegjelenithetoVerzio(){
        DummyVersionedObject dvo = new DummyVersionedObject();
        String res = VersionedObjectUtil.getMegjelenithetoVerzio(dvo);
        //createdBy az defaultbol sosem null hanem egy " " ures string
        assertEquals(" -  ", res);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss");
        String fd = sdf.format(now);
        dvo.setCreatedAt(now);
        dvo.setCreatedBy("teszt");
        dvo.setVerzioNev("tesztverzio");
        res = VersionedObjectUtil.getMegjelenithetoVerzio(dvo);
        assertEquals(fd+" - teszt: tesztverzio", res);
    }

    private static final class DummyVersionedObject extends VersionedObjectBase{
        private DummyVersionedObject(Integer objectVersion) {
            super(objectVersion);
        }

        private DummyVersionedObject() {
            super();
        }
    }
}