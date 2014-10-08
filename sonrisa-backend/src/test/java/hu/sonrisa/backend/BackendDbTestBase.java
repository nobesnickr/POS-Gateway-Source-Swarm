/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sonrisa.backend;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author joe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional(propagation = Propagation.REQUIRED)
public class BackendDbTestBase extends BackendTestBase{
    
}
