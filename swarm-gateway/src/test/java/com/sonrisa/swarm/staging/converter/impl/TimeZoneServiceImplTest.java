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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.retailpro.dao.impl.RpStoreDaoImpl;
import com.sonrisa.swarm.retailpro.model.RpStoreEntity;

/**
 *
 * @author joe
 */
public class TimeZoneServiceImplTest {

    private TimeZoneServiceImpl timeZoneService;
    private RpStoreDaoImpl rpStoreDao;
    
    @Before
    public void init(){
        rpStoreDao = mock(RpStoreDaoImpl.class);
        timeZoneService = new TimeZoneServiceImpl(rpStoreDao);
    }
    
    /**
     * Test case: only the timeZone property is set on the rpStore entity.
     * 
     * @throws ParseException 
     */
    @Test
    public void onlyTimeZoneSet() throws ParseException {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        // timezone of the store, it could be any valid timezone id, e.g. UTC, UTC+2, GMT, GMT-8 etc...
        final String storeTimeZoneId = "UTC";
        final TimeZone defaultTZ = TimeZone.getDefault();
        final TimeZone storeTZ = TimeZone.getTimeZone(storeTimeZoneId);
        
        // diff between the default and the store's timezone
        final long diffBetweenTimeZones = diffBetweenTimeZones(storeTZ, defaultTZ);
        
        final Date now = new Date();
        final Date expectedDate = new Date(now.getTime() + diffBetweenTimeZones);
        
        // creates a store with the given timezone
        final RpStoreEntity rpStore = mockRpStoreEntity(storeTimeZoneId, null);
        when(rpStoreDao.findBySbsNoAndStoreNoAndSwarmId(anyString(), anyString(), anyString())).thenReturn(rpStore);
        
        // creates an invoice with the default timezone
        final InvoiceEntity invoice = mockInvoice(now);
        final InvoiceStage invoiceStage = mockInvoiceStage();
        
        // corrects the timezone on the invoice
        timeZoneService.correctInvoiceTs(new StoreEntity(), invoice, invoiceStage);
        
        assertEquals(df.format(expectedDate), df.format(invoice.getTs().getTime()));
        
    }
    
    /**
     * Only the timeOffset property is set on the rpStore entity. (With a negative value.)
     * 
     * @throws ParseException 
     */
    @Test
    public void onlyOffsetSet1() throws ParseException {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        final Date date = new Date();
        final Date expectedDate = new Date(date.getTime() - 3600*1000);
        
        final RpStoreEntity rpStore = mockRpStoreEntity(null, -60);
        final InvoiceEntity invoice = mockInvoice(date);
        final InvoiceStage invoiceStage = mockInvoiceStage();
        
        when(rpStoreDao.findBySbsNoAndStoreNoAndSwarmId(anyString(), anyString(), anyString())).thenReturn(rpStore);
        timeZoneService.correctInvoiceTs(new StoreEntity(), invoice, invoiceStage);
        
        assertEquals(df.format(expectedDate.getTime()), df.format(invoice.getTs()));
        
    }
    
    /**
     * Only the timeOffset property is set on the rpStore entity. (With a positive value.)
     * 
     * @throws ParseException 
     */   
    @Test
    public void onlyOffsetSet2() throws ParseException {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        final Date date = new Date();
        final Date expectedDate = new Date(date.getTime() + 3*3600*1000);
        
        final RpStoreEntity rpStore = mockRpStoreEntity(null, 180);
        final InvoiceEntity invoice = mockInvoice(date);
        final InvoiceStage invoiceStage = mockInvoiceStage();
        
        when(rpStoreDao.findBySbsNoAndStoreNoAndSwarmId(anyString(), anyString(), anyString())).thenReturn(rpStore);
        timeZoneService.correctInvoiceTs(new StoreEntity(), invoice, invoiceStage);
        
        assertEquals(df.format(expectedDate.getTime()), df.format(invoice.getTs()));
        
    }    
    
    private RpStoreEntity mockRpStoreEntity(String timeZone, Integer offset){
        RpStoreEntity entity = new RpStoreEntity();
        entity.setTimeZone(timeZone);
        entity.setTimeOffset(offset);
        
        return entity;
    }
    
    private InvoiceStage mockInvoiceStage(){
        return MockTestData.mockInvoice("lsId", "custId", Long.MIN_VALUE, "99");
    }
    
    private InvoiceEntity mockInvoice(Date ts){
        InvoiceEntity ie = new InvoiceEntity();
        ie.setTs(ts);
        
        return ie;
    }
    
    /**
     * Returns the current offset in millisec between the two time zone.
     * 
     * @param zone1
     * @param zone2
     * @return 
     */
    private long diffBetweenTimeZones(TimeZone zone1, TimeZone zone2) {
        long currentTime = System.currentTimeMillis();
        int zone1Offset = zone1.getOffset(currentTime);
        int zone2Offset = zone2.getOffset(currentTime);
        return zone2Offset - zone1Offset;
    }
}