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

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import hu.sonrisa.backend.model.util.LocaleUtil;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 *
 * @author Golyo
 */
public class PdfTableHandler extends ITextPdfPTableHandler {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat(ExportStyleHandler.DATE_FORMAT);
    protected static final String KELT_MEZO = "mezo.kelt";
    protected static final String TEL_MEZO = "mezo.tel";
    protected static final String NEV_MEZO = "mezo.nev";
    protected static final String HEADER_ONKORMANYZATI = "header.onkormanyzati";
    protected static final String HEADER_KOZPONTI = "header.kozponti";
    protected static final String IRANYITO_MEZO = "mezo.iranyito";
    protected static final String SZAMJEL_MEZO = "mezo.szamjel";
    protected static final String PIR_MEZO = "mezo.pir";
    protected static final String SZEKTOR_MEZO = "mezo.szektor";
    protected static final String MEGYE_MEZO = "mezo.megye";
    protected static final String FEJEZET_MEZO = "mezo.fejezet";
    protected static final String ALFEJEZET_MEZO = "mezo.alfejezet";
    protected static final String AHT_MEZO = "mezo.aht";
    protected static final String CIMREND_MEZO = "mezo.cimrend";
    protected static final String PUK_MEZO = "mezo.puk";
    protected static final String SZAKAG_MEZO = "mezo.szakag";
    protected static final String NEVSZEKHELY_MEZO = "mezo.nevszekhely";
    protected static final String PONTOZOTT_MEZO = "mezo.pontozott";
    protected static final String PONTOZOTT_MEZO2 = "mezo.pontozott2";
    protected static final String FELELOS_MEZO = "mezo.felelos";
    protected static final String VEZETO_MEZO = "mezo.vezeto";
    protected static final String KAPCSTARTO_MEZO = "mezo.kapcstarto";
    protected static final String ELLENORZO_MEZO = "mezo.ellenorzo";
    protected static final String REGSZAM_MEZO = "mezo.regszam"; 
    protected static final String TAGSZAM_MEZO = "mezo.tagszam"; 
    protected static final String VAGY_MEZO = "mezo.vagy"; 

    protected PdfWriter writer;

    public PdfTableHandler(Document doc, PdfWriter writer, BaseFont baseFont) {
        super(doc, baseFont);
        this.writer = writer;
        setMaxColNo(12);
        document.setMargins(15, 15, 80, 40);
    }

    public void updatePageSize(Rectangle rectangle) {
        document.setPageSize(rectangle);
    }

    @Override
    protected Map<String, ITextPdfCellStyle> createStyleMap() {
        Map<String, ITextPdfCellStyle> sm = super.createStyleMap();
        //Font titleFont = createFont(18, Font.NORMAL);
        Font font = createFont(12, Font.NORMAL);
        Font sfont = createFont(6, Font.NORMAL);
        Font lfont = createFont(18, Font.NORMAL);
        java.awt.Color headColor = new java.awt.Color(192, 192, 192);

        sm.put(ExportStyleHandler.HEADER_STYLE, new ITextPdfCellStyle(font, PdfPCell.ALIGN_CENTER, headColor, 0f));
        sm.put(ExportStyleHandler.LEFT_STYLE, new ITextPdfCellStyle(font, PdfPCell.ALIGN_LEFT, Color.white, 0f));
        sm.put(ExportStyleHandler.CENTER_STYLE, new ITextPdfCellStyle(font, PdfPCell.ALIGN_CENTER, Color.white, 0f));
        sm.put(ExportStyleHandler.RIGHT_STYLE, new ITextPdfCellStyle(font, PdfPCell.ALIGN_RIGHT, Color.white, 0f));
        sm.put(ExportStyleHandler.DATE_STYLE, new ITextPdfCellStyle(font, PdfPCell.ALIGN_LEFT, new SimpleDateFormat(ExportStyleHandler.DATE_FORMAT), Color.white, 0f));

        sm.put(ExportStyleHandler.L_HEADER_STYLE, new ITextPdfCellStyle(lfont, PdfPCell.ALIGN_CENTER, headColor, 0f));
        sm.put(ExportStyleHandler.S_HEADER_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_CENTER, headColor, 0f));
        sm.put(ExportStyleHandler.S_LEFT_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_LEFT, Color.white, 0f));
        sm.put(ExportStyleHandler.S_CENTER_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_CENTER, Color.white, 0f));
        sm.put(ExportStyleHandler.S_RIGHT_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_RIGHT, Color.white, 0f));
        sm.put(ExportStyleHandler.S_INT_NUMBER_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_RIGHT, LocaleUtil.INTEGER_FORMAT, Color.white, 0f));
        sm.put(ExportStyleHandler.S_DEC_NUMBER_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_RIGHT, LocaleUtil.DECIMAL_FORMAT, Color.white, 0f));
        sm.put(ExportStyleHandler.S_DATE_STYLE, new ITextPdfCellStyle(sfont, PdfPCell.ALIGN_LEFT, new SimpleDateFormat(ExportStyleHandler.DATE_FORMAT), Color.white, 0f));
        //Font headerFont = new Font(bf, 6, Font.NORMAL);
        //Font font = new Font(bf, 6, Font.NORMAL);
        //Font numberFont = new Font(bf, 6, Font.NORMAL);
        return sm;
    }

    protected void addText(Document document, PdfContentByte cb,
            String value, int size, int align, float x, float y, float w, float h) {
        cb.beginText();
        cb.setFontAndSize(getBaseFont(), size);
        cb.showTextAligned(align, value, x + 10, document.top() - document.bottom() - y - h + 5, 0);
        cb.endText();
    }

    @Override
    protected ITextPdfCellStyle findStyle(String style, BorderType type, boolean checkExists) {
        ITextPdfCellStyle rets = super.findStyle(style, type, false);
        if (rets == null) {
            int p = style.indexOf(ExportStyleHandler.STYLE_DELIM);
            if (p < 0) {
                throw new RuntimeException(style);
            }
            FontStilus s = FontStilus.valueOf(style.substring(p + 1));
            String prefix = style.substring(0, p);
            ITextPdfCellStyle olds = super.findStyle(prefix, type, true);
            Font newf = createFont(6, Font.NORMAL);
            switch (s) {
                case BOLD: {
                    newf.setSize(7);
                    newf.setStyle(Font.BOLD);
                    break;
                }
                case ITALIC: {
                    newf.setStyle(Font.ITALIC);
                    break;
                }
                case NORMAL: {
                    //Should use without postfix
                    break;
                }
                default: {
                    throw new RuntimeException();
                }
            }
            rets = new ITextPdfCellStyle(newf, olds.getHorizontalAlignment(), olds.getFormat(), olds.getBackgroundColor(), olds.getLeading());
            styleMap.put(style, rets);
        }
        return rets;
    }
}
