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
package hu.sonrisa.backend.async;

/**
 *
 * @author Golyo
 */
public enum FileTipus {

    /**
     * 
     */
    PDF(".pdf", "application/pdf"),
    /**
     * 
     */
    XLS(".xls", "application/excel"),
    /**
     * 
     */
    XLSX(".xlsx", "application/x-excel"),
    /**
     * 
     */
    ZIP(".zip", "application/zip"),
    /**
     * 
     */
    DBF(".dbf", "application/dbase"),
    /**
     * 
     */
    DOC(".doc", "application/msword"),
    /**
     * 
     */
    CSV(".csv", "text/csv"), 
    /**
     * 
     */
    TEXT(".txt", "text/plain");
    
    private String extension;
    private String mimeType;

    private FileTipus(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    /**
     * 
     * @return
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * 
     * @return
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 
     * @param fileName
     * @return
     */
    public String getFileName(String fileName) {
        return fileName + extension;
    }
}
