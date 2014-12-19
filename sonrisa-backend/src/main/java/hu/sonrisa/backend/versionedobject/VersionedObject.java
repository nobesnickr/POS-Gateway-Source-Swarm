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
package hu.sonrisa.backend.versionedobject;

import hu.sonrisa.backend.model.FingerPrinted;
import hu.sonrisa.backend.model.PersistenceAware;

/**
 * Verziózott objektumok kezelését lehetővé tevő interfész
 * 
 * @author Palesz
 */
public interface VersionedObject extends FingerPrinted, PersistenceAware<String> {

    /**
     * @return Az objektum adott verziójának egyedi azonosítója.
     * Ezzel az egy azonosító önmagában azonosítja az objektumnak
     * az adott verzióját a teljes rendszerben.
     */
     String getId();

     /**
      * 
      * @param id
      */
     void setId(String id);

    /**
     * @return Az objektum szülő verziójának az egyedi
     * azonosítóját adja vissza.
     */
     String getSzuloId();

     /**
      * 
      * @param szuloId
      */
     void setSzuloId(String szuloId);

    /**
     * @return Visszaadja a verziózott objektum kódját (a típuson belül
     * egyedileg azonosítja az objektumot, viszont nem azonosítja az
     * objektum verzióját).
     * (Pl.: '02', 'TELTIP', ...)
     */
     String getKod();

     /**
      * 
      * @param kod
      */
     void setKod(String kod);

    /**
     * @return Visszaadja az objektum adott verziójának ember számára
     * értelmezhető azonosítáját (pl.: '2009 v1').
     */
     String getVerzioNev();

     /**
      * 
      * @param verzioNev
      */
     void setVerzioNev(String verzioNev);

    /**
     * @return Visszaadja az objektum adott verziójához fűzött megjegyzést.
     */
     String getVerzioMegjegyzes();

     /**
      * 
      * @param verzioMegjegyzes
      */
     void setVerzioMegjegyzes(String verzioMegjegyzes);
     
     /** Visszaadja, hogy (logikailag) torolt-e az objektum.  */
     boolean isDeleted();
     
     /** Objektum logikai torlesere szolgal. */
     void setDeleted(boolean deleted);
}