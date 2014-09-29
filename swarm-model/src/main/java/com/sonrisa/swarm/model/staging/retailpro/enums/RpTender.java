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
package com.sonrisa.swarm.model.staging.retailpro.enums;

/**
 * WRONG-DESIGN, Retail Pro specific enum, should not be in common model module
 * 
 * Reason: The {@link InvoiceStagingConverter} depends on {@link RpTender}
 * 
 * Retail Pro legacy tenders
 */
public enum RpTender {
   
    CASH(0),
    CHECK(1),
    CREDIT_CARD(2),
    CASH_ON_DELIVERY(3),
    CHARGE(4),
    STORE_CREDIT(5),
    SPLIT(6),
    DEPOSIT(7),
    PAYMENTS(8),
    
    /**
     * Gift certificates and gift cards are not counted among actual sales
     */
    GIFT_CERTIFICATE(9),
    GIFT_CARD(10),
    DEBIT_CARD(11),
    
    /**
     * Tender type 12 is foreign currency inside of Rpro9 CORE CODE in memory. 
     * In the database, though, foreign currency is identified by two conditions - 
     * tender type is CASH (0) and currency does not match base currency for that subsidiary. 
     */
    FOREIGN_CURRENCY(12),  
    TRAVELER_CHECK(13),
    FOREIGN_CHECK(14),
    CENTRAL_STORE_CREDIT(15),
    
    /**
     * Should be ignored, and not included among actual sales
     */
    CENTRAL_GIFT_CARD(16),
    CENTRAL_GIFT_CERTIFICATE(17),
    UNKNOWN_TENDER(-1);
    

    /**
     * Tender code as sent by the Retail Pro plugin
     */
    private int lsTenderCode;
    
    private RpTender(int lsTenderCode){
        this.lsTenderCode = lsTenderCode;
    }

    /**
     * @return The lsTenderCode
     */
    public int getLsTenderCode() {
        return lsTenderCode;
    }
    
    /**
     * Parses the integer argument as an RpTender
     * @param value Legacy Retail Pro tender code
     * @return RpTender with legacy tender code matching value
     */
    public static RpTender parseTender(int value){
        for(RpTender tender : RpTender.values()){
            if(tender.getLsTenderCode() == value){
                return tender;
            }
        }
        return UNKNOWN_TENDER;
    }
    
    /**
     * Parses the string argument as an RpTender
     * @param value Legacy Retail Pro tender code
     * @return RpTender with legacy tender code matching value
     */
    public static RpTender parseTender(String value){
        try {
            return parseTender(Integer.parseInt(value));
        } catch (NumberFormatException e){
            return UNKNOWN_TENDER;
        }
    }
}
