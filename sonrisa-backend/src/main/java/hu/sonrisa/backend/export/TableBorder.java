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
package hu.sonrisa.backend.export;

import java.util.ArrayList;
import java.util.List;

/**
 * Táblázat border leíró
 * @author Golyo
 */
public class TableBorder {
    private int tlRow;
    private int brRow;
    private int tlCol;
    private int brCol;
    private boolean innerBordered;

    private List<TableBorder> innerBorders;

    /**
     * Táblázat border leíró létrehozás
     * @param tlRow bal felső sor
     * @param tlCol bal felső oszlop
     * @param brRow jobb alsó sor
     * @param brCol jobb alsó oszlop
     * @param innerBordered
     */
    public TableBorder(int tlRow, int tlCol, int brRow, int brCol, boolean innerBordered) {
        this.innerBordered = innerBordered;
        this.tlRow = tlRow;
        this.tlCol = tlCol;
        this.brRow = brRow;
        this.brCol = brCol;
        innerBorders = null;
    }

    /**
     * Belső border hozzáadás
     * @param tlRow
     * @param tlCol
     * @param brRow
     * @param brCol
     * @param innerBordered
     * @return
     */
    public TableBorder addInnerBorder(int tlRow, int tlCol, int brRow, int brCol, boolean innerBordered) {
        if (innerBorders == null) {
            innerBorders = new ArrayList<TableBorder>();
        }
        TableBorder ret = new TableBorder(tlRow, tlCol, brRow, brCol, innerBordered);
        innerBorders.add(ret);
        return ret;
    }

    /**
     * Border visszaadása az adott oszlophoz és sorhoz
     * @param row
     * @param col
     * @return
     */
    public BorderType getBorderType(int row, int col) {
        BorderType bd = new BorderType();
        fillBorderType(row, col, bd);
        return bd;
    }

    private void fillBorderType(int row, int col, BorderType bd) {
        if (row >= tlRow && row <= brRow && col >= tlCol && col <= brCol ) {
            if (row == tlRow || innerBordered) {
                bd.setBorder(BorderType.BorderSide.TOP);
            } else {
                bd.clearBorder(BorderType.BorderSide.TOP);
            }
            if (row == brRow || innerBordered) {
                bd.setBorder(BorderType.BorderSide.BOTTOM);
            } else {
                bd.clearBorder(BorderType.BorderSide.BOTTOM);
            }
            if (col == tlCol || innerBordered) {
                bd.setBorder(BorderType.BorderSide.LEFT);
            } else {
                bd.clearBorder(BorderType.BorderSide.LEFT);
            }
            if (col == brCol || innerBordered) {
                bd.setBorder(BorderType.BorderSide.RIGHT);
            } else {
                bd.clearBorder(BorderType.BorderSide.RIGHT);
            }
            if (innerBorders != null) {
                for (TableBorder b: innerBorders) {
                    b.fillBorderType(row, col, bd);
                }
            }
        }
    }
}
