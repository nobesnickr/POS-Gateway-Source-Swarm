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
package hu.sonrisa.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A Spring context-hez statikus hozzáférést biztosító bean.
 *
 * @author Joe
 */
@Component
public class AppContextProvider implements ApplicationContextAware {

    /** Referencia a spring context-re. */
    private static ApplicationContext ctx;
    private final static Logger LOGGER = LoggerFactory.getLogger(AppContextProvider.class);

    /**
     * 
     */
    public AppContextProvider() {
        LOGGER.info("constructed");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("Setting application context: " + applicationContext);
        AppContextProvider.ctx = applicationContext;
    }

    /**
     * Visszaadja a spring contextust.
     * @return 
     * 
     */
    public static ApplicationContext getContext() {
        if (ctx == null) {
            throw new IllegalStateException();
        }
        return ctx;
    }
}
