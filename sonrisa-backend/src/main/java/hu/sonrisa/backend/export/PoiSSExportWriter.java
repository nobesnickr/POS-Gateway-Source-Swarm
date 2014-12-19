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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Golyo
 */
public class PoiSSExportWriter implements ExportWriter<Workbook> {

    private Workbook workBook;
    private OutputStream outStream;

    public PoiSSExportWriter(OutputStream outStream, boolean isHssf) {
        workBook = isHssf ? new HSSFWorkbook() : new XSSFWorkbook();
        this.outStream = outStream;
    }

    public PoiSSExportWriter(InputStream inStream, OutputStream outStream) {
        try {            
            workBook = WorkbookFactory.create(inStream);
        } catch (IOException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00012, e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00012, e);
        }
        this.outStream = outStream;
    }

    @Override
    public void open() {

    }
    
    @Override
    public void close() {
        try {
            workBook.write(outStream);
        } catch (IOException e) {
            throw new RuntimeException(BackendExceptionConstants.BEND_00013, e);
        } finally {
            IOUtils.closeQuietly(outStream);
        }
    }

    @Override
    public Workbook getDocument() {
        return workBook;
    }
}