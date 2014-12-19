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
package hu.sonrisa.backend.model;
/**
 * A MapAdapter nevű, egy Map-et Collection-ként "láttató" osztályba pakolható
 * objektumoknak ezt az interfészt implementálniuk kell. A MapAdapter a 
 * getKey() által visszaadott érték alapján teszi majd a Map-be az értékeket.
 * 
 * Az osztály elsősorban akkor hasznos, ha egy Collection jellegű mezőt egyből
 * indexelni is akarunk és egy közvetlen elérésű Map-en át is el akarjuk tudni 
 * érni az elemeit.
 * 
 * @author cserepj
 */
public interface Key {

    /**
     * A MapAdapter ez alapján a string alapján épít Map-et ebből az objektumból.
     * 
     * @return 
     */
    String getKey();

}