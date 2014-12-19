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

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;
import java.text.Format;

/**
 * Itext cella stílus elkészítéséhez
 * @author Golyo
 */
public class ITextPdfCellStyle {
    private static final Font DEAFULT_FONT = new Font();

    private Color backgroundColor;
    private Integer horizontalAlignment;
    private Float leading;
    private Format format;
    private Font font;

    /**
     * Cella stílus elkészítése
     * @param font
     * @param horizontalAlignment
     * @param backgroundColor
     * @param leading
     */
    public ITextPdfCellStyle(Font font, Integer horizontalAlignment, Color backgroundColor, Float leading) {
        this(font, horizontalAlignment, null, backgroundColor, leading);
    }

    /**
     * Cella stílus elkészítése
     * @param font
     * @param horizontalAlignment
     * @param format
     * @param backgroundColor
     * @param leading
     */
    public ITextPdfCellStyle(Font font, Integer horizontalAlignment, Format format, Color backgroundColor, Float leading) {
        this.font = font != null ? font : DEAFULT_FONT;
        this.leading = leading != null ? leading : Float.NaN;
        this.horizontalAlignment = horizontalAlignment;
        this.backgroundColor = backgroundColor;
        this.format = format;
    }

    /**
     * Cellán a stípus beállítása
     * @param cell
     * @param value
     */
    public void addPhraseToCell(PdfPCell cell, Object value) {
        if (value != null) {
            cell.setPhrase(createPhrase(value));
            if (horizontalAlignment != null) {
                cell.setHorizontalAlignment(horizontalAlignment);
            }
            if (backgroundColor != null) {
                cell.setBackgroundColor(backgroundColor);
            }
        } else {
            cell.setPhrase(new Phrase(leading, null, font));
        }
    }

    public void addPhraseToParagraph(Paragraph paragraph, Object value) {
        paragraph.add(createPhrase(value));
        paragraph.setAlignment(horizontalAlignment);
    }
    
    protected Phrase createPhrase(Object value) {
        String val = format != null ? format.format(value) : value.toString();
        return new Phrase(leading, val, font);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Font getFont() {
        return font;
    }

    public Format getFormat() {
        return format;
    }

    public Integer getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public Float getLeading() {
        return leading;
    }

}
