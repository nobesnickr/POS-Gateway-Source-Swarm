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
package hu.sonrisa.backend.async;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sonrisa
 */
public class HatterFolyamatTest {

    public HatterFolyamatTest() {
    }

    @After
    public void clearList() {
    }

    @Test
    public void testFolyamatLeall() {
        HatterFolyamat hf = new HatterFolyamat() {

            @Override
            protected void futtatas() {
                // nem csinál semmit
            }

            @Override
            public boolean isMegszakithato() {
                return false;
            }

        };

        hf.run();

        // a folyamat futás után leáll
        Assert.assertTrue(hf.isFinished());
    }

    @Test
    public void testMegszakitas() throws InterruptedException {
        HatterFolyamat hf = new HatterFolyamat() {

            @Override
            protected void futtatas() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (isMegszakithato()) {
                            return;
                        }
                    } catch (InterruptedException ex) {
                    }
                }
            }

            @Override
            public boolean isMegszakithato() {
                return true;
            }

        };

        new Thread(hf).start();

        Assert.assertFalse(hf.isFinished());
        HatterFolyamat.stopFolyamat(hf.getId());
        Thread.sleep(1000);
        Assert.assertTrue(hf.isFinished());
    }
}
