/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Sonrisa Informatikai Kft. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sonrisa.
 *
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package hu.sonrisa.backend.model;

import java.util.Date;

/**
 * Azon objektumok egységes kezelésére szolgál, melyeken a változtató személyek
 * és időpontok eltárolásra kerülnek.
 * 
 * @author cserepj
 */
public interface FingerPrinted {

    /**
     * 
     * @return
     */
    String getCreatedBy();

   /**
    * 
    * @return
    */
   String getModifiedBy();

   /**
    * 
    * @return
    */
   Date getCreatedAt();

   /**
    * 
    * @return
    */
   Date getModifiedAt();

   /**
    * 
    * @param creator
    */
   void setCreatedBy(String creator);

   /**
    * 
    * @param modifier
    */
   void setModifiedBy(String modifier);

   /**
    * 
    * @param date
    */
   void setCreatedAt(Date date);

   /**
    * 
    * @param date
    */
   void setModifiedAt(Date date);
}