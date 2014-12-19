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

import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Exportáláshoz használt, külön Thread-et igénylő háttérben futó folyamat.
 *
 * @author Borosan
 */
public abstract class ExportFolyamat extends HatterFolyamat implements IFolyamat {

    /**
     * Az exportálással létrehozott állomány neve
     */
    private String fileName;
    /**
     * Az exportálással létrehozott állomány típusa
     */
    private FileTipus fileTipus;
    private FileOutputStream stream;
    private File file;

    /**
     * Constructor
     *
     * @param fileName
     * @param fileTipus
     */
    public ExportFolyamat(String fileName, FileTipus fileTipus) {
        this.fileName = fileName;
        this.fileTipus = fileTipus;
        try {
            file = File.createTempFile(fileName, fileTipus.getExtension());
            this.stream = new FileOutputStream(file);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * File name
     *
     * @return
     */
    public String getFinalFileName() {
        return fileTipus.getFileName(fileName);
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public boolean isMegszakithato() {
        return true;
    }
    
    public File getEredmeny() {
        return file;
    }
    
    protected FileOutputStream getStream() {
        return stream;
    }
    
    @Override
    protected void onFinished() {
        IOUtils.closeQuietly(stream);
    }
}
