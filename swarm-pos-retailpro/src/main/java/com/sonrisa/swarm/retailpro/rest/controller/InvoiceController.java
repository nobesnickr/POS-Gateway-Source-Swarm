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

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.common.rest.controller.BaseSwarmController;
import com.sonrisa.swarm.retailpro.service.RpInvoiceService;
import com.sonrisa.swarm.retailpro.util.mapper.EntityHolder;

/**
 * MVC controller responsible for handling the invoice requests.
 *
 * @author joe
 */
@Controller
@RequestMapping(InvoiceController.URI)
public class InvoiceController extends BaseSwarmController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    /** The URI of this controller. */
    public static final String URI = "/items/invoice";

    /**
     * Retail Pro invoices service responsible for processing JSON and
     * saving content into the staging tables
     */
    @Autowired
    private RpInvoiceService rpInvoiceService;

    /**
    * Directory where the gateway writes log files
    */
    @Value("${user.home}/swarm/log/rpClient/")
    private String logDirectory;

    /**
     * Jackson object mapper mapping raw string to JSON
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * On PUT request we should log the request body (JSON), and add that JSON to the processing queue.
     * The data is processed on the other end of the queue.
     * @param swarmId the client's swarmId
     * @param posSoftware the client's software version
     * @param rawJson the data to process. Should be JSON.
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<String> create(
            @RequestHeader(value = "SwarmId", required = false) String swarmId,
            @RequestHeader(value = "Pos-Software", required = false) String posSoftware, 
            @RequestBody String rawJson) throws Exception {


        // Prepare logger
        final String appenderName = "seq" + swarmId;
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(InvoiceController.class);
        DailyRollingFileAppender appender = new DailyRollingFileAppender(
                                                    new PatternLayout("%d{ISO8601} - %m%n"),
                                                    getFilePathFromSourceName(swarmId), 
                                                    "'.'yyyy-MM-dd");

        // Set logger to log only on the DEBUG level
        appender.setName(appenderName);
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);

        // Log raw JSON
        logger.debug(rawJson);
        logger.removeAppender(appender);

        // Create internal DTO from JSON
        final Map<String, Object> jsonMap = objectMapper.readValue(rawJson, Map.class);
        EntityHolder result = rpInvoiceService.processMap(swarmId, jsonMap);

        // Save internal DTO into the staging tables
        rpInvoiceService.writeToStage(result);
        
        // Logging
        LOGGER.info("Inserting {} customers {} invoices {} invoice lines and {} products into stage for {}",
                getListSize(result.getCustomers()), getListSize(result.getInvoices()),
                getListSize(result.getItems()), getListSize(result.getProducts()), swarmId);
        
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    /**
     * Get the log file's path based on a swarmId
     */
    private String getFilePathFromSourceName(final String swarmId) {
        StringBuilder builder = new StringBuilder();
        builder.append(logDirectory);
        builder.append(swarmId);
        builder.append("-client-seq.log");
        return builder.toString();
    }

    /**
     * Null-safe size for list
     */
    private int getListSize(List<?> list) {
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    @Override
    public Logger logger() {
        return LOGGER;
    }
}
