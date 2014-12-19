/*
 *   Copyright (c) 2012 Sonrisa Informatikai Kft. All Rights Reserved.
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

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class PdfTableHandlerTest {
    
    /**
     * Unit teszt, ami azt ellenőrzi, hogy a "származtatott" stílusok (amik 
     * egy létező stílust egészítenek ki pl félkövérré) megfelelően megkapják-e
     * a stílus értékeket. Lsd: #4707
     * 
     * 
     * @throws DocumentException
     * @throws IOException 
     */
    @Test
    public void testFindStyle() throws DocumentException, IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final ITextPdfWriter pdfWriter = new ITextPdfWriter(PageSize.A4, outStream);
        final BaseFont font = BaseFont.createFont();
        final PdfTableHandler handler = new PdfTableHandler(pdfWriter.getDocument(), pdfWriter.getWriter(), font);                
        
        try{
            // ez kell, hogy inicializálódjon a stílus map
            handler.init();

            // alap int stílus
            String stilus = ExportStyleHandler.S_INT_NUMBER_STYLE;
            findAndAssertStyle(handler, stilus);

            // alap int stílus + normal
            stilus = ExportStyleHandler.S_INT_NUMBER_STYLE + ExportStyleHandler.STYLE_DELIM + FontStilus.NORMAL;
            findAndAssertStyle(handler, stilus);

            // alap int stílus + italic
            stilus = ExportStyleHandler.S_INT_NUMBER_STYLE + ExportStyleHandler.STYLE_DELIM + FontStilus.ITALIC;
            findAndAssertStyle(handler, stilus);

            // alap int stílus + bold
            stilus = ExportStyleHandler.S_INT_NUMBER_STYLE + ExportStyleHandler.STYLE_DELIM + FontStilus.BOLD;
            findAndAssertStyle(handler, stilus);
        }finally{            
            outStream.close();            
        }
    }
    
    /**
     * Elkéri a handler-től a kapott stílust, és ellenőrzi, hogy minden ki
     * van-e töltve rajta.
     * 
     * @param handler
     * @param styleName 
     */
    private void findAndAssertStyle(final PdfTableHandler handler, final String styleName){
        ITextPdfCellStyle style = handler.findStyle(styleName, null, true);
        
        assertNotNull("Nincs meg a stilus. Stilus neve: " + styleName, style);
        assertNotNull("Nincs kitoltve a format a stiluson. Stilus neve: " + styleName, style.getFormat());
        assertNotNull("Nincs kitoltva a font a stiluson. Stilus neve: " + styleName, style.getFont());
        assertNotNull("Nincs kitoltva a bg color a stiluson. Stilus neve: " + styleName, style.getBackgroundColor());
        assertNotNull("Nincs kitoltva a horizontal alignment a stiluson. Stilus neve: " + styleName, style.getHorizontalAlignment());
        assertNotNull("Nincs kitoltva a leading a stiluson. Stilus neve: " + styleName, style.getLeading());
    }
}
