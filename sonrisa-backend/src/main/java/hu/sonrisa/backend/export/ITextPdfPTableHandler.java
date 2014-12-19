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

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import hu.sonrisa.backend.export.AbstractTableHandler;
import hu.sonrisa.backend.export.BorderType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Itext táblázat kezelő
 * @author Golyo
 */
public class ITextPdfPTableHandler extends AbstractTableHandler<Document, PdfPTable, Integer, PdfPCell, ITextPdfCellStyle> {

    private BaseFont baseFont;
    private boolean hasElolap = false;
    private boolean firstTable = true;

    /**
     * Itext táblázat kezelő létrehozása
     * @param document
     * @param baseFont
     */
    public ITextPdfPTableHandler(Document document, BaseFont baseFont) {
        super(document);
        if (baseFont == null) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00008);
        }
        setMaxColNo(12);
        this.baseFont = baseFont;
    }

    public BaseFont getBaseFont() {
        return baseFont;
    }

    @Override
    protected Map<String, ITextPdfCellStyle> createStyleMap() {
        Map<String, ITextPdfCellStyle> sm = new HashMap<String, ITextPdfCellStyle>();
        Font def = createFont(getDefaultFontSize(), Font.NORMAL);
        sm.put(null, new ITextPdfCellStyle(def, PdfPCell.ALIGN_LEFT, Color.WHITE, null));
        return sm;
    }

    protected Font createFont(int size, int style) {
        return new Font(baseFont, size, style);
    }

    @Override
    protected PdfPCell findCell(PdfPTable table, int rowIdx, int cellIdx) {
        PdfPRow prow = table.getRow(rowIdx);
        if (prow.getCells().length > cellIdx) {
            return prow.getCells()[cellIdx];
        } else {
            return null;
        }
    }

    @Override
    protected PdfPCell createCell(PdfPTable table, Integer row, int colIdx, Object value,
            ITextPdfCellStyle style, int colspan, BorderType bt) {
        PdfPCell cell = new PdfPCell();
        style.addPhraseToCell(cell, value);
        cell.setColspan(colspan);
        if (bt != null) {
            cell.setBorder(bt.getBorders());
        }
        table.addCell(cell);
        return cell;
    }

    @Override
    protected PdfPCell createCell(PdfPTable table, Integer row, int colIdx, PdfPCell cell) {
        table.addCell(cell);
        return cell;
    }

    @Override
    protected void updateCellValue(PdfPCell cell, Object value, ITextPdfCellStyle style) {
        style.addPhraseToCell(cell, value);
    }

    @Override
    protected Integer createNewRow(PdfPTable table, int rowIdx) {
        return table.getRows().size();
    }

    @Override
    protected PdfPTable createTable(String name, float[] columnWidths,
            int headerRows, float defaultColumnWidth) {
        for (int i = 0; i < columnWidths.length; i++) {
            if (columnWidths[i] == 0) {
                columnWidths[i] = defaultColumnWidth;
            }
        }
        PdfPTable newt = new PdfPTable(columnWidths);
        newt.setHeaderRows(headerRows);
        return newt;
    }

    @Override
    protected void closeTable(PdfPTable table) {
        if (firstTable) {
            if (hasElolap) {
                document.newPage();
            }
            firstTable = false;
        } else {
            document.newPage();
        }
        try {
            document.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00009, e);
        }
    }

    @Override
    protected void finishRow(PdfPTable table, int colIdx) {
        table.completeRow();


        int idx = tables.indexOf(table);
        idx++;
        if (idx < tables.size()) {
            PdfPRow row = table.getRow(table.getRows().size() - 1);
            for (int i = idx; i < tables.size(); i++) {
                PdfPTable act = tables.get(i);
                act.completeRow();
                for (int col = 0; col < fixColNo; col++) {
                    act.addCell(row.getCells()[col]);
                }
                act.completeRow();
            }
        }
    }

    @Override
    public void changeLandscape(boolean landscape) {
        if (landscape) {
            document.setPageSize(PageSize.A4.rotate());
        } else {
            document.setPageSize(PageSize.A4);
        }
    }

    @Override
    public void newParagraph(Object value, String style) {
        ITextPdfCellStyle cellStyle = findStyle(style, null, true);
        Paragraph p = new Paragraph();
        cellStyle.addPhraseToParagraph(p, value);
        try {
            document.add(p);
        } catch (DocumentException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00010, e);
        }

    }

    public boolean isHasElolap() {
        return hasElolap;
    }

    public void setHasElolap(boolean hasElolap) {
        this.hasElolap = hasElolap;
    }

}
