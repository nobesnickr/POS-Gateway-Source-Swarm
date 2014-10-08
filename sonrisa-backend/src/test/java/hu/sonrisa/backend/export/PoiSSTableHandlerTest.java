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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class PoiSSTableHandlerTest {

    /**
     * Teszteset a #4698 ticket-hez. Excel exportnál a cella tartalma nem lehet
     * hosszabb, mint 32767 karakter, mert az excel max ennyit enged.
     *
     * Teszteset: A maximálisnál hosszabb szöveget hoz létre, amit beleír egy
     * cellába. Majd egy másik cellába is beleír, amit utána egy szintén túl
     * hosszú szöveggel update-el. (Csak, hogy mindkét metódus tesztelve
     * legyen.)
     *
     * Elvárt működés: A megengedett hossznál hosszabb értékeket levágja és
     * "..."-t tesz a végekre. (Ott vágja le, hogy a "..." még pont odaférjen.)
     *
     * @throws IOException
     */
    @Test
    public void maximumLengthOfCellTest() throws IOException {
        // init
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PoiSSExportWriter writer = new PoiSSExportWriter(stream, true);
        PoiSSTableHandler handler = new XlsTableHandler(writer.getDocument());
        handler.init();

        // create sheet & row
        float[] wtf = {10, 10, 10};
        final String SHEET_NAME = "teszt";
        Sheet sheet = handler.createTable(SHEET_NAME, wtf, 0, 30);
        Row row = handler.createNewRow(sheet, 0);
        CellStyle style = createDummyStyle(writer.getDocument());

        // kell egy hossszabb string, mint ami még megengedett
        final String cellValue = generateStringValue('a', handler.getMaxCellValueLength() + 1);
        // ez a rövidített érték, amit majd a végén várunk.
        final String expCellValue = generateStringValue('a', handler.getMaxCellValueLength() - 3) + "...";

        final String cellValue2 = generateStringValue('b', handler.getMaxCellValueLength() + 1);
        final String cellValue3 = generateStringValue('c', handler.getMaxCellValueLength() + 1);
        // ez a rövidített érték, amit majd a végén várunk.
        final String expCellValue3 = generateStringValue('c', handler.getMaxCellValueLength() - 3) + "...";

        // cellába ír, nem szállhat el
        int colIndex = 0;
        handler.createCell(sheet, row, colIndex, cellValue, style, 0, null);
        // cellát update-el, nem szállhat el
        colIndex++;
        Cell cell = handler.createCell(sheet, row, colIndex, cellValue2, style, 0, null);
        handler.updateCellValue(cell, cellValue3, style);

        // lezár mindent
        handler.endTable();
        writer.close();
        stream.close();

        // ellenőriz
        Workbook wb = new HSSFWorkbook(new ByteArrayInputStream(stream.toByteArray()));
        assertEquals(1, wb.getNumberOfSheets());
        Sheet sh = getCheckedSheet(wb, 0);
        assertEquals(SHEET_NAME, sh.getSheetName());

        Cell cellTest = getCheckedCell(wb, 0, 0, 0);
        assertEquals(expCellValue, cellTest.getStringCellValue());
        
        Cell cellTest3 = getCheckedCell(wb, 0, 0, 1);
        assertEquals(expCellValue3, cellTest3.getStringCellValue());

    }

    /**
     * A kért hosszúságú string-et állít elő.
     *
     * @param length
     * @return
     */
    private String generateStringValue(final char c, int length) {
        StringBuilder str = new StringBuilder();
        for (long i = 0; i < length; i++) {
            str.append(c);
        }
        return str.toString();
    }

    private CellStyle createDummyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setWrapText(true);
        style.setFillPattern(CellStyle.NO_FILL);
        return style;
    }

    private Sheet getCheckedSheet(Workbook wb, int sheetIdx) {
        Sheet sheet = wb.getSheetAt(sheetIdx);
        assertNotNull(sheet);
        return sheet;
    }

    
    private Cell getCheckedCell(Row row, int cellIdx) {
        Cell cell = row.getCell(cellIdx);
        assertNotNull(cell);
        return cell;
        
    }
    
    protected Cell getCheckedCell(Workbook wb, int sheetIdx, int rowIdx, int cellIdx) {
        return getCheckedCell(getCheckedRow(getCheckedSheet(wb, sheetIdx), rowIdx), cellIdx);
    }   
    
     protected Row getCheckedRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        assertNotNull(row);
        return row;
    }
     
      
   
}
