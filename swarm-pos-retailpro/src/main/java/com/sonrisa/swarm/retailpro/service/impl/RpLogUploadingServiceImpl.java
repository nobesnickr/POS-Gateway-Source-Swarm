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
package com.sonrisa.swarm.retailpro.service.impl;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.retailpro.service.RpLogUploadingService;
import com.sonrisa.swarm.retailpro.util.FileUtils;

/**
 * Implementation of the {@link RpLogUploadingService} class
 * 
 * @author barna
 */
@Service
public class RpLogUploadingServiceImpl implements RpLogUploadingService {

    private static final Logger DEFAULT_LOGGER = LoggerFactory
            .getLogger(RpLogUploadingServiceImpl.class);

    /**
     * Directory where the gateway writes log files
     */
    @Value("${user.home}/swarm/log/rpClient/")
    private String logDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String sourceName, String message) {

        if (sourceName == null) {
            throw new IllegalArgumentException("sourceName must not be null");
        }

        DEFAULT_LOGGER.debug("{} says: {}", sourceName, message);

        FileUtils.createDirectoryIfDoesntExist(logDirectory);

        // See:
        // http://stackoverflow.com/questions/6998323/log4j-dynamic-configuration
        org.apache.log4j.Logger logger = org.apache.log4j.Logger
                .getLogger(RpLogUploadingServiceImpl.class);
        PatternLayout layout = new PatternLayout("%d{ISO8601} - %m%n");

        try {
            final String path = getFilePathFromSourceName(sourceName);
            DEFAULT_LOGGER.debug("Trying to save client log for {} to {}", sourceName, path);
            DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, path, "'.'yyyy-MM-dd");
            appender.setName(sourceName);
            logger.addAppender(appender);

            logger.setLevel(Level.INFO);
            logger.info(message);

            logger.removeAppender(sourceName);
        } catch (IOException e) {
            DEFAULT_LOGGER.error("Failed to save client log file to {} for {} with message: {}", logDirectory, sourceName, message, e);
        }
    }

    /**
     * Get the log file's path based on a swarmId
     */
    private String getFilePathFromSourceName(final String sourcename) {
        StringBuilder builder = new StringBuilder();
        builder.append(logDirectory);
        builder.append(sourcename);
        builder.append("-client.log");
        return builder.toString();
    }

    public String getUploadedLogDirectory() {
        return logDirectory;
    }

    public void setUploadedLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }
}
