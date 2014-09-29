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
package com.sonrisa.swarm.admin.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.posintegration.service.ApiService;
import com.sonrisa.swarm.posintegration.service.model.ApiEntity.ApiType;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Simple UI for the {@link StatusServiceController} and {@link RpStatusServiceController}
 * 
 * @author Barnabas
 */
@Controller
public class PosStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PosStatusController.class);
    
    /** API service for reading API */
    @Autowired
    private ApiService apiService;

    /**
     * Returns demo page for the POS status
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.GET, value = "/admin/status")
    public @ResponseBody ModelAndView rpStatusPage() throws Exception {
        LOGGER.debug("Pos status was requested");
        return new ModelAndView("admin/pos_status", getModel());
    }
    
    /**
     * Returns Retail Pro demo page for the POS status
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.GET, value = "/admin/retailpro/status")
    public @ResponseBody ModelAndView posStatusPage() throws Exception {
        LOGGER.debug("Pos status was requested");
        return new ModelAndView("admin/rp_pos_status", getModel());
    }

    /**
     * Creates model and view to be returned by the controller
     * @return
     */
    private ModelMap getModel(){

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("pullApis", apiService.findManyByType(ApiType.PULL_API));
        modelMap.addAttribute("retailproApis", apiService.findManyByType(ApiType.RETAILPRO_API));
        modelMap.addAttribute("statusValues", StoreStatus.values());
        
        // Print server date and time
        modelMap.addAttribute("date", ISO8061DateTimeConverter.dateToMysqlString(new Date()));
        
        return modelMap;
    }
}
