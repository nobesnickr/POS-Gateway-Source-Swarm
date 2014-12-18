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
package com.sonrisa.swarm.retailpro.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonrisa.swarm.retailpro.rest.model.InterfaceDescription;


/**
 * MVC controller responsible for handling the requests
 * for the meta description of the SWAM REST API.
 *
 * @author joe
 */
@Controller
public class InterfaceDescriptionController extends BaseRetailProController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDescriptionController.class);
    
    /** A key in the version.properties file. */
    private static final String VERSION_KEY = "project.version";
    /** A key in the version.properties file. */
    private static final String BUILD_TIMESTAMP_KEY = "build.timestamp";
    /** This is used if the project version or the build timestamp 
     * can not be defined from the version property file. */
    private static final String UNKOWN = "unkown";
    
    /** The interface version, which equals with the maven artifact version. */
    private final String INTERFACE_VERSION;
    /** Date and time of the last build. */
    private final String BUILD_TIMESTAMP;

    /**
     * Constructor.
     */
    public InterfaceDescriptionController() {   
        final Properties versionProps = loadVersionProps();            
        INTERFACE_VERSION = versionProps.getProperty(VERSION_KEY, UNKOWN);
        BUILD_TIMESTAMP = versionProps.getProperty(BUILD_TIMESTAMP_KEY, UNKOWN);
    }
    
    

    /**
     * Gets the largest supported interface version. 
     * <pre>
     * URI: [prot]://[host]/version (GET)
     * 
     * Request body: - 
     * Response body: API interface version number in [major].[minor] form. 
     * 
     * Eg.:
     * { 
     *      „interfaceVersion”: „1.08” 
     * }
     * </pre>
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/version")
    public @ResponseBody InterfaceDescription version() {
        return new InterfaceDescription(INTERFACE_VERSION, BUILD_TIMESTAMP);
    }

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @Override
    public Logger logger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @Override
    public boolean needsSwarmId() {
        return false;
    }

    /**
     * This method loads the version.properties file,
     * which contains meta information about the
     * current version of the application.
     * 
     * @return 
     */
    private Properties loadVersionProps() {
        final Properties versionProps = new Properties();

        InputStream inStream = null;
        try {
            inStream = InterfaceDescriptionController.class.getResourceAsStream("/version.properties");
            versionProps.load(inStream);
        } catch (final IOException ex) {
            LOGGER.warn("An exception occures during the loading of the version.properties.", ex);
        } finally {
            if (inStream != null) {
                IOUtils.closeQuietly(inStream);
            }
        }

        return versionProps;
    }                
}
