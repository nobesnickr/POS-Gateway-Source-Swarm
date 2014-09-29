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
 * Retail Pro legacy receipt types
 */
public enum RpReceiptType {
    SALES(0),
    RETURN(2),
    CHECK_IN(3),
    CHECK_OUT(4),
    LOST_SALE(6),
    HIGH_SECURITY(7),
    OPEN_REGISTER(10),
    CLOSE_REGISTER(11),
    PAYOUT(12),
    MANAGER_OVERRIDE(13);
        
    /** Legacy RPro code */
    private int lsReceiptType;
    
    private RpReceiptType(int lsTenderCode){
        this.lsReceiptType = lsTenderCode;
    }

    public int getLsReceiptType() {
        return lsReceiptType;
    }
}
