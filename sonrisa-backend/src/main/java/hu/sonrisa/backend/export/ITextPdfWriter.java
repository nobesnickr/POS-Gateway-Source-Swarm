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
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import hu.sonrisa.backend.exception.BackendExceptionConstants;
import java.io.OutputStream;

/**
 * IText táblázat író
 * @author Golyo
 */
public class ITextPdfWriter implements ExportWriter<Document>  {

    private Document document;
    private PdfWriter writer;
    /**
     * Constructor for creating an IText based pdf writer for generating pdf documents
     * @param rectangle / PageSize (e.g PageSize.A4)
     * @param outStream  / outputstream to write to 
     */
    public ITextPdfWriter(Rectangle rectangle, OutputStream outStream) {
        document = new Document(rectangle);
        try {
            writer = PdfWriter.getInstance(document, outStream);
        } catch (DocumentException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00011, e);
        }
    }

    /**
     * 
     * @return 
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /**
     * A writer
     * @return 
     */
    public PdfWriter getWriter() {
        return writer;
    }

    /**
     * 
     */
    @Override
    public void close() {
        document.close();
        writer.close();
    }
    /**
     * 
     */
    @Override
    public void open() {
        document.open();
    }
    
    /**
     * Új oldal
     * @param rectangle 
     */
    public void newPage(Rectangle rectangle) { 
        document.setPageSize(rectangle); 
        document.newPage(); 
    }
}
