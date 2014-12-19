/*
 *  *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.export;

/**
 * Táblázat kezelő
 * @author Golyo
 */
public interface TableHandler {

    /**
     * Inicializálás
     */
     void init();
    /**
     * Új táblázat
     * @param name Táblázat neve
     * @param columnWidths Oszlopok szélessége
     * @param headerRows header sorok száma
     * @param fixColNo Fix oszlopok száma
     * @param style Stílus
     * @param border Border
     */
     void newTable(String name, float[] columnWidths, int headerRows,
                                    int fixColNo, String style, TableBorder border);
    /**
     * Táblázat vége
     */
     void endTable();

    /**
     * Sor kihagyás
     * @param no
     */
     void skipRow(int no);
    /**
     * Új sor
     * @param style
     */
     void newRow(String style);

    /**
     * Cella kihagyás
     * @param no
     */
     void skipCell(int no);
    /**
     * Új oszlop
     * @param value
     * @param style
     * @param colspan
     */
     void newCell(Object value, String style, int colspan);

    /**
     * Visszzamenőleges felülírás az adott sorban
     * @param celIdx
     * @param value
     * @param style
     */
     void updateInRowData(int celIdx, Object value, String style);

     /**
      * 
      * @param landscape
      */
     void changeLandscape(boolean landscape);

     /**
      * 
      * @param o
      */
     void setHeaderDescriptor(Object o);
     /**
      * 
      * @return
      */
     Object getHeaderDescriptor();

     /**
      * 
      * @param value
      * @param style
      */
     void newParagraph(Object value, String style);   
}
