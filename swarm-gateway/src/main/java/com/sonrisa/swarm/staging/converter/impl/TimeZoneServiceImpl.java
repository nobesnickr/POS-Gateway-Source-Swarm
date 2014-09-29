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
package com.sonrisa.swarm.staging.converter.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.common.util.DateUtil;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.retailpro.dao.impl.RpStoreDaoImpl;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;
import com.sonrisa.swarm.staging.converter.TimeZoneService;

/**
 *
 * @author joe
 */
@Service
public class TimeZoneServiceImpl implements TimeZoneService {
    
    private RpStoreDaoImpl rpStoreDao;

    @Autowired
    public TimeZoneServiceImpl(RpStoreDaoImpl rpStoreDao) {
        this.rpStoreDao = rpStoreDao;
    }
  
    /**
     * {@inheritDoc}
     */
    public void correctInvoiceTs(StoreEntity store, InvoiceEntity invoice, InvoiceStage stgInvoice){
        // has the store got time zone information?
        final String storeTimeZone = store.getTimeZone();
        if (StringUtils.hasLength(storeTimeZone)){
            // TODO: should we handle this case as well?
        }
        
        
        // is it an invoice from RetailPro?
        final RpStoreEntity rpStore = rpStoreDao.findBySbsNoAndStoreNoAndSwarmId(
                    stgInvoice.getLsSbsNo(),
                    stgInvoice.getLsStoreNo(),
                    stgInvoice.getSwarmId());
        if (rpStore != null){
            // has the RpStore got time zone information?
            final String rpStoreTimeZone = rpStore.getTimeZone();
            if (StringUtils.hasLength(rpStoreTimeZone)){
                Date correctedTs = DateUtil.setTimeZoneWithoutConversion(invoice.getTs(), rpStoreTimeZone);
                invoice.setTs(correctedTs);
            }
            
            // has the RpStore got time offset value?
            final Integer rpStoreTimeOffset = rpStore.getTimeOffset();
            if (rpStoreTimeOffset != null){
                Calendar cal = Calendar.getInstance();
                cal.setTime(invoice.getTs());
                
                cal.add(Calendar.MINUTE, rpStoreTimeOffset);
                invoice.setTs(cal.getTime());
            }
        }
    }
}
