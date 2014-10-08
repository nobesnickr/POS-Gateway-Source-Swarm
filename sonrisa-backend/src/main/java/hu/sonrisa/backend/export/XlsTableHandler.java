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

import hu.sonrisa.backend.exception.BackendExceptionConstants;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Xls Stílusok definiálására és általános xls beállítások
 * @author Golyo
 */
public class XlsTableHandler extends PoiSSTableHandler {

    /**
     *
     * @param document
     */
    public XlsTableHandler(Workbook document) {
        super(document);
        setMaxColNo(255);
    }

    @Override
    protected Map<String, CellStyle> createStyleMap() {
        Map<String, CellStyle> sm = super.createStyleMap();
        sm.put(ExportStyleHandler.LEFT_STYLE, createStyle(CellStyle.ALIGN_LEFT, (short) 10));
        sm.put(ExportStyleHandler.CENTER_STYLE, createStyle(CellStyle.ALIGN_CENTER, (short) 10));
        sm.put(ExportStyleHandler.RIGHT_STYLE, createStyle(CellStyle.ALIGN_RIGHT, (short) 10));
        sm.put(ExportStyleHandler.COURIER8_STYLE, createTypeStyle(ExportStyleHandler.COURIER_FONTNAME, (short) 8, true));
        CellStyle titleStyle = createStyle(CellStyle.ALIGN_CENTER, (short) 18);
        CellStyle centerBordered = createStyle(CellStyle.ALIGN_CENTER, (short) 10);
        centerBordered.setBorderBottom(CellStyle.BORDER_MEDIUM);
        centerBordered.setBorderTop(CellStyle.BORDER_MEDIUM);
        centerBordered.setBorderLeft(CellStyle.BORDER_MEDIUM);
        centerBordered.setBorderRight(CellStyle.BORDER_MEDIUM);
        sm.put(ExportStyleHandler.CENTER_BORDERED_STYLE, centerBordered);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sm.put(ExportStyleHandler.TITLE_STYLE, titleStyle);
        CellStyle headerStyle = createStyle(CellStyle.ALIGN_CENTER, (short) 12);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        sm.put(ExportStyleHandler.HEADER_STYLE, headerStyle);
        sm.put(ExportStyleHandler.DATE_STYLE, createDateStyle(CellStyle.ALIGN_LEFT, (short)10, ExportStyleHandler.DATE_FORMAT));
        sm.put(ExportStyleHandler.S_LEFT_STYLE, sm.get(ExportStyleHandler.LEFT_STYLE));
        sm.put(ExportStyleHandler.S_CENTER_STYLE, sm.get(ExportStyleHandler.CENTER_STYLE));
        sm.put(ExportStyleHandler.S_RIGHT_STYLE, sm.get(ExportStyleHandler.RIGHT_STYLE));
        sm.put(ExportStyleHandler.S_HEADER_STYLE, sm.get(ExportStyleHandler.HEADER_STYLE));
        sm.put(ExportStyleHandler.S_INT_NUMBER_STYLE, createNumericStyle(CellStyle.ALIGN_RIGHT, (short) 10, false));
        sm.put(ExportStyleHandler.S_DEC_NUMBER_STYLE, createNumericStyle(CellStyle.ALIGN_RIGHT, (short) 10, true));
        return sm;
    }

    @Override
    protected Row createNewRow(Sheet table, int rowIdx) {
        return super.createNewRow(table, rowIdx);
    }

    private CellStyle createNumericStyle(short alignment, short fontsize, boolean tort) {
        CellStyle style = createStyle(alignment, fontsize);
        style.setDataFormat(document.createDataFormat().getFormat(tort
                ? ExportStyleHandler.DECIMAL_FORMAT : ExportStyleHandler.INTEGER_FORMAT));
        return style;
    }

    private CellStyle createDateStyle(short alignment, short fontsize, String format) {
        CellStyle style = createStyle(alignment, fontsize);
        CreationHelper createHelper = document.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat(format));
        return style;
    }
    private CellStyle createStyle(short alignment, short fontsize) {
        CellStyle style = document.createCellStyle();
        style.setAlignment(alignment);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setWrapText(true);
        style.setFillPattern(CellStyle.NO_FILL);
        Font font = document.createFont();
        font.setFontHeightInPoints(fontsize);
        style.setFont(font);
        return style;
    }

    /**
     *
     * @param cell
     * @param value
     * @param style
     */
    protected void setCellValue(Cell cell, Object value, String style) {
        cell.setCellStyle(findStyle(style, (BorderType) null, true));
        setCellValue(cell, value, true);
    }

    /**
     *
     * @param style
     * @param stilus
     * @return
     */
    protected String getStyleKey(String style, String stilus) {
        return style + ExportStyleHandler.STYLE_DELIM + stilus;
    }

    @Override
    protected CellStyle findStyle(String style, BorderType type, boolean checkExists) {
        CellStyle rets = super.findStyle(style, type, false);
        if (rets == null) {
            int p = style.indexOf(ExportStyleHandler.STYLE_DELIM);
            if (p < 0) {
                throw new RuntimeException(BackendExceptionConstants.BEND_00015 + style);
            }
            String s = style.substring(p + 1);
            String prefix = style.substring(0, p);
            rets = document.createCellStyle();
            rets.cloneStyleFrom(super.findStyle(prefix, type, true));
            Font newf = cloneFont(rets);
            if ("bold".equalsIgnoreCase(s)) {
                newf.setBoldweight(Font.BOLDWEIGHT_BOLD);
            } else if ("italic.".equalsIgnoreCase(s)) {
                newf.setItalic(true);
            }
            rets.setFont(newf);
            styleMap.put(style, rets);
        }
        return rets;
    }

    private Font cloneFont(CellStyle style) {
        Font orig = document.getFontAt(style.getFontIndex());
        Font newf = document.createFont();
        newf.setFontHeightInPoints(orig.getFontHeightInPoints());
        newf.setColor(orig.getColor());
        newf.setFontName(orig.getFontName());
        newf.setUnderline(orig.getUnderline());
        return newf;
    }
    
    private CellStyle createTypeStyle(String fontName, short fontSize, boolean wrap) {
        CellStyle style = document.createCellStyle();
        Font font = document.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setFontName(fontName);
        style.setFont(font);
        style.setWrapText(wrap);
        return style;
    }
}
