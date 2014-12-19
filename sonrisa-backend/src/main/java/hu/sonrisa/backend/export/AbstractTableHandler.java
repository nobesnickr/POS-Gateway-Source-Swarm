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
package hu.sonrisa.backend.export;

import hu.sonrisa.backend.exception.BackendExceptionConstants;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Táblázat kezelő osztály, ami segít a különböző excel, pdf, 
 * ... apiknak a TableHandler implementálásában
 * @param <D> 
 * @param <R> 
 * @param <T> 
 * @param <S> 
 * @param <C> 
 * @author Golyo
 */
public abstract class AbstractTableHandler<D, T, R, C, S> implements TableHandler {

    /**
     * Dokumentum
     */
    protected D document;
    /**
     * Táblák (max col number mentén darabolás)
     */
    protected List<T> tables;
    /**
     * Aktuális tábla
     */
    protected T actTable;
    /**
     * Header sorok
     */
    protected int headerRows;
    /**
     * Tábla neve
     */
    private String tableName;
    /**
     * Táblázat stílus
     */
    private S defaultTableStyle;
    /**
     * Sor stílus
     */
    private S defaultRowStyle;
    /**
     * Aktuális oszlop index
     */
    private int actColIdx;
    /**
     * Akruális sor index
     */
    private int actRowIdx;
    /**
     * Aktuális sor
     */
    private R actRow;
    /**
     * Táblázat border leíró
     */
    private TableBorder tableBorder;
    /**
     * Oszlopok szélessége
     */
    private float[] columnWidths;
    /**
     * Fix oszlopok száma. Ezeket minden új táblázatba klónozza
     */
    protected int fixColNo;
    /**
     * Táblázat vége
     */
    private boolean endTableOk;
    /**
     * Stílus térkép
     */
    protected Map<String, S> styleMap;
    /**
     * Táblázat nevében legyen a sorszáma is ?
     */
    private boolean tnameWithIdx;
    /**
     * Maximum oszlopok száma
     */
    private int maxColNo;
    /**
     * Alap szélesség
     */
    private float defaultWidth;
    /**
     * Alap font méret
     */
    private int defaultFontSize;
    /**
     * 
     */
    private Object headerDescriptor;

    /**
     * 
     * @param document
     */
    public AbstractTableHandler(D document) {
        this.document = document;
        maxColNo = Integer.MAX_VALUE;
    }

    @Override
    public void init() {
        styleMap = createStyleMap();
        tables = new LinkedList<T>();
        defaultFontSize = 12;
        endTableOk = true;
    }

    /**
     * Új sor készítése
     * @param table
     * @param rowIdx
     * @return
     */
    protected abstract R createNewRow(T table, int rowIdx);

    /**
     * Sor befejezése
     * @param table
     * @param colIdx
     */
    protected abstract void finishRow(T table, int colIdx);

    /**
     * Cella készítése
     * @param table
     * @param row
     * @param colIdx
     * @param value
     * @param style
     * @param colspan
     * @param bt
     * @return
     */
    protected abstract C createCell(T table, R row, int colIdx, Object value, S style, int colspan, BorderType bt);

    /**
     * Cella készítése
     * @param table
     * @param row
     * @param colIdx
     * @param cell
     * @return
     */
    protected abstract C createCell(T table, R row, int colIdx, C cell);

    /**
     * Cella megtalálása
     * @param table
     * @param rowIdx
     * @param cellIdx
     * @return
     */
    protected abstract C findCell(T table, int rowIdx, int cellIdx);

    /**
     * Táblázat készítése
     * @param name
     * @param columnWidths
     * @param headerRows
     * @param defaultWidth
     * @return
     */
    protected abstract T createTable(String name, float[] columnWidths, int headerRows, float defaultWidth);

    /**
     * Cella érték felülírása cella alapján
     * @param cell
     * @param value
     * @param style
     */
    protected abstract void updateCellValue(C cell, Object value, S style);

    /**
     * Táblázat lezárása
     * @param table
     */
    protected abstract void closeTable(T table);

    /**
     * Stílus térkép elkészítése
     * @return
     */
    protected abstract Map<String, S> createStyleMap();

    /**
     * 
     * @return
     */
    public float getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * 
     * @param defaultWidth
     */
    public void setDefaultWidth(float defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    /**
     * 
     * @return
     */
    public D getDocument() {
        return document;
    }

    /**
     * 
     * @return
     */
    public int getMaxColNo() {
        return maxColNo;
    }

    /**
     * 
     * @param maxColNo
     */
    public void setMaxColNo(int maxColNo) {
        this.maxColNo = maxColNo;
    }

    /**
     * 
     * @return
     */
    public int getDefaultFontSize() {
        return defaultFontSize;
    }

    /**
     * 
     * @param defaultFontSize
     */
    public void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    @Override
    public final void endTable() {
        finishRow(actTable, actColIdx);
        for (T t : tables) {
            closeTable(t);
        }
        tables.clear();
        endTableOk = true;
    }

    @Override
    public void updateInRowData(int celIdx, Object value, String style) {
        BorderType bt = tableBorder != null ? tableBorder.getBorderType(actRowIdx, celIdx) : null;
        int tidx = (celIdx - fixColNo) / (maxColNo - fixColNo);
        C cell = findCell(tables.get(tidx), actRowIdx, celIdx);
        if (cell != null) {
            updateCellValue(cell, value, findStyle(style, bt, true));
        }
    }

    @Override
    public final void newTable(String tableName, float[] columnWidths, int headerRows,
            int fixColNo, String style, TableBorder tableBorder) {
        if (!endTableOk) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00007);
        } else {
            endTableOk = false;
        }
        this.headerRows = headerRows;
        this.tableBorder = tableBorder;
        this.columnWidths = new float[columnWidths.length];
        System.arraycopy(columnWidths, 0, this.columnWidths,
                0, columnWidths.length);
        this.fixColNo = fixColNo;
        this.tableName = tableName;
        tnameWithIdx = columnWidths.length > maxColNo;
        defaultTableStyle = style != null ? getStyle(style, true) : getStyle(null, true);
        actTable = createNextTable();
        actRowIdx = -1;
        actColIdx = -1;
    }

    private T createNextTable() {
        int from = tables.size() * (maxColNo - fixColNo);
        int cno = columnWidths.length - from;
        cno = Math.min(maxColNo, cno);
        float[] actw = new float[cno];
        System.arraycopy(columnWidths, 0, actw, 0, fixColNo);
        System.arraycopy(columnWidths, from + fixColNo, actw, fixColNo, cno - fixColNo);
        String tname = tnameWithIdx ? tableName + "." + (tables.size() + 1) : tableName;
        T newt = createTable(tname, actw, headerRows, defaultWidth);
        tables.add(newt);
        return newt;
    }

    @Override
    public void newCell(Object value, String style, int colspan) {
        actColIdx++;
        BorderType bt = tableBorder != null ? tableBorder.getBorderType(actRowIdx, actColIdx) : null;
        S cstyle = findStyle(style, bt, true);
        C cell = null;
        while (actColIdx + colspan - 1 >= maxColNo) {
            int tidx = tables.indexOf(actTable);
            int maxActIdx = fixColNo;
            if (colspan > 1) {
                int origColIdx = tidx * (maxColNo - fixColNo) + actColIdx;
                colspan = Math.min(columnWidths.length - origColIdx, colspan);
                maxActIdx = Math.min(fixColNo, actColIdx);
                int actColspan = Math.min(maxColNo - actColIdx, colspan);
                cell = createCell(actTable, actRow, actColIdx, value, cstyle, actColspan, bt);
                colspan -= actColspan;
                colspan += fixColNo - actColIdx;
            }
            T holdTable = actTable;
            actTable = tables.size() > tidx + 1 ? tables.get(tidx + 1) : createNextTable();
            newRowOnAct(); //set act row
            //actColIdx = 0; 
            //uj sor kezdodik, ezert lesz 0 az actColIdx
            for (actColIdx = 0; actColIdx < maxActIdx; actColIdx++) {
                cell = findCell(holdTable, actRowIdx, actColIdx);
                cell = createCell(actTable, actRow, actColIdx, cell);
            }
            actColIdx = maxActIdx;
        }
        createCell(actTable, actRow, actColIdx, value, cstyle, colspan, bt);
        actColIdx += colspan - 1;
    }

    @Override
    public void newRow(String style) {
        defaultRowStyle = style != null ? getStyle(style, true) : defaultTableStyle;
        actRowIdx++;
        finishRow(actTable, actColIdx);
        actTable = tables.get(0);
        newRowOnAct();
    }

    private void newRowOnAct() {
        actRow = createNewRow(actTable, actRowIdx);
        actColIdx = -1;
    }

    /**
     * 
     * @param styleKey
     * @param bt
     * @param checkExists
     * @return
     */
    protected S findStyle(String styleKey, BorderType bt, boolean checkExists) {
        if (styleKey != null) {
            return getStyle(styleKey, checkExists);
        } else {
            return defaultRowStyle;
        }
    }

    @Override
    public void skipCell(int no) {
        throw new UnsupportedOperationException(BackendExceptionConstants.BEND_00004);
    }

    @Override
    public void skipRow(int no) {
        throw new UnsupportedOperationException(BackendExceptionConstants.BEND_00004);
    }

    private S getStyle(String styleKey, boolean checkExists) {
        S style = styleMap.get(styleKey);
        if (style == null && checkExists) {
            throw new IllegalArgumentException(styleKey + BackendExceptionConstants.BEND_00006);
        }
        return style;
    }

    /**
     * 
     * @param o
     */
    @Override
    public void setHeaderDescriptor(Object o) {
        headerDescriptor = o;
    }

    /**
     * 
     * @return
     */
    @Override
    public Object getHeaderDescriptor() {
        return headerDescriptor;
    }
}
