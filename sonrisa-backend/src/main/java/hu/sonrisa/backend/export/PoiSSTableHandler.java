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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Pois általános (xls, és xlsx) táblázat kezelő.
 * @author Golyo
 */
public class PoiSSTableHandler extends AbstractTableHandler<Workbook, Sheet, Row, Cell, CellStyle> {

    /**
     * Az excel-ben a cellába írható tartalom maximális hossza. Lsd:
     * http://office.microsoft.com/en-us/excel-help/excel-specifications-and-limits-HP005199291.aspx
     */
    private static final int MAXIMUM_LENGTH_OF_CELL_CONTENT = 32767;
    private HashMap<String, CellStyle> borderMap;
    private short borderStyle = CellStyle.BORDER_MEDIUM;

    public PoiSSTableHandler(Workbook document) {
        super(document);
        borderMap = new HashMap<String, CellStyle>();
        setMaxColNo(255);
    }

    @Override
    protected void closeTable(Sheet table) {
        //Nothing to do;
    }

    @Override
    protected Map<String, CellStyle> createStyleMap() {
        Map<String, CellStyle> sm = new HashMap<String, CellStyle>();
        sm.put(null, getDocument().createCellStyle());
        return sm;
    }

    @Override
    protected Cell createCell(Sheet table, Row row, int colIdx, Object value, CellStyle style, int colspan, BorderType bt) {
        Cell cell = row.createCell(colIdx);
        cell.setCellStyle(style);
        setCellValue(cell, value, false);
        if (colspan > 1) {
            table.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), colIdx, colIdx + colspan-1));
        }
        return cell;
    }

    protected void setCellValue(Cell cell, Object value, boolean force) {
        if (value != null) {
            if (Date.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue((Date)value);
            } else if (Number.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue(((Number)value).doubleValue());
            } else {
                // le kell vágni, ha hosszabb mint az excelben megengedett cella hossz 
                final String abbrString = StringUtils.abbreviate(value.toString(), getMaxCellValueLength());
                cell.setCellValue(abbrString);
            }
        } else if (force) {
            cell.setCellValue("");
        }
    }

    @Override
    protected void updateCellValue(Cell cell, Object value, CellStyle style) {
        cell.setCellStyle(style);
        setCellValue(cell, value, true);
    }
    
    @Override
    protected Cell createCell(Sheet table, Row row, int colIdx, Cell cell) {
        Cell ret = row.createCell(colIdx);
        ret.setCellStyle(cell.getCellStyle());
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: {
                ret.setCellValue(cell.getNumericCellValue());
                break;
            }
            case Cell.CELL_TYPE_BOOLEAN: {
                ret.setCellValue(cell.getBooleanCellValue());
                break;
            }
            default:
                ret.setCellValue(cell.getStringCellValue());
        }
        ret.setCellType(cell.getCellType());
        return ret;
    }

    @Override
    protected Row createNewRow(Sheet table, int rowIdx) {
        Row r = table.createRow(rowIdx);
//        if (rowIdx+1 >= headerRows) {
//            actTable.createFreezePane(0, headerRows);
//        }
        return r;
    }

    @Override
    protected Sheet createTable(String name, float[] columnWidths, int headerRows, float defaultColumnWidth) {
        borderMap.clear();
        Sheet sheet = document.createSheet(name);
        sheet.createFreezePane(0, headerRows);
        if (defaultColumnWidth > 0) {
            sheet.setDefaultColumnWidth((int)defaultColumnWidth);
        }
        if (columnWidths != null) {
            for (int i=0; i<columnWidths.length; i++) {
                if (columnWidths[i] > 0) {
                    int w = (int)(columnWidths[i] * 70);
                    sheet.setColumnWidth(i, w);
                }
            }
        }
        int idx = document.getNumberOfSheets()-1;
        handleNewSheetHeader(sheet.getHeader(), idx, getHeaderDescriptor());
        handleNewSheetFooter(sheet.getFooter(), idx);
        return sheet;
    }

    @Override
    protected Cell findCell(Sheet table, int rowIdx, int cellIdx) {
        return table.getRow(rowIdx).getCell(cellIdx);
    }

    @Override
    protected void finishRow(Sheet table, int colIdx) {
        //Do nothing
    }


    @Override
    protected CellStyle findStyle(String style, BorderType type, boolean checkExists) {
        if (type != null && type.hasBorders()) {
            String key = style + "-" + type.getBorders();
            CellStyle ret = borderMap.get(key);
            if (ret == null) {
                ret = document.createCellStyle();
                ret.cloneStyleFrom(super.findStyle(style, type, true));
                ret.setBorderBottom(type.isBorder(BorderType.BorderSide.BOTTOM) ? borderStyle : CellStyle.BORDER_NONE);
                ret.setBorderTop(type.isBorder(BorderType.BorderSide.TOP) ? borderStyle : CellStyle.BORDER_NONE);
                ret.setBorderLeft(type.isBorder(BorderType.BorderSide.LEFT) ? borderStyle : CellStyle.BORDER_NONE);
                ret.setBorderRight(type.isBorder(BorderType.BorderSide.RIGHT) ? borderStyle : CellStyle.BORDER_NONE);
                borderMap.put(key, ret);
            }
            return ret;
        } else {
            return super.findStyle(style, type, checkExists);
        }
    }
 
    /**
     * Megadja, hogy egy cella tartalma legfeljebb milyen hosszú lehet.
     * @return 
     */
    public int getMaxCellValueLength() {
        return MAXIMUM_LENGTH_OF_CELL_CONTENT;
    }

    @Override
    public void changeLandscape(boolean landscape) {        
    }

    protected void handleNewSheetHeader(Header header, int sheetIdx, Object headerObject) {
    }

    protected void handleNewSheetFooter(Footer header, int sheetIdx) {
    }

    @Override
    public void newParagraph(Object value, String style) {
        throw new UnsupportedOperationException();
    }
}