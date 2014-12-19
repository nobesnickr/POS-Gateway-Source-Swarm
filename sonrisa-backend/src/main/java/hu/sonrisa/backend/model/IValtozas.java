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
 * Egy általános interfész a változásokhoz (pl.: a {@link Diffable}
 * interfészt implementálók generálnak ilyen változásokat).
 *
 * @author Palesz
 */
public interface IValtozas<T> {

    //definíción:
    //   - változást indukál (sor hozzáadás, törlés, oszlop hozzáadás, törlés)
    //      ---> szabályok utánahúzása
    //   - ennek egy másik változást kell indukálnia

    //példányon:
    //   - adatok utánahúzása (sor hozzádás, törlés, oszlop hozzáadás, törlés)
    //   - szabályváltozás miatt újratöltés, adattilalmak számítása, ...
    //

    /**
     * Végrehajtja a változtatást az adott objektumon.
     *
     * @param obj
     */
    void valtoztatas(T obj);

}
