/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author racka
 */
public class CSVReaderImpl implements CSVReader{

    private BufferedReader reader;
    private String preFetch;
    private String separator;

    /**
     * 
     * @return
     * @throws IOException
     */
    @Override
    public String[] getNextSplit() throws IOException {
        String[] splitted = null;
        if (hasNext()) {
            splitted = preFetch.split(separator);
            preFetch = null;
        }
        return splitted;

    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        if (preFetch == null && reader.ready()) {
            preFetch = reader.readLine();
        }
        return preFetch != null;
    }

    /**
     * 
     * @param in
     * @param separator
     * @param charset
     * @throws UnsupportedEncodingException
     */
    @Override
    public void setTarget(InputStream in, String separator, String charset) throws UnsupportedEncodingException{
        this.reader = new BufferedReader(new InputStreamReader(in, charset));
        this.separator = separator;
    }

    /**
     * 
     * @param reader
     * @param separator
     */
    public  void setTarget(BufferedReader reader, String separator) {
        this.reader = reader;
        this.separator = separator;
    }

    /**
     * 
     * @return
     */
    protected BufferedReader getReader() {
        return reader;
    }

    /**
     * 
     * @return
     */
    protected String getSeparator() {
        return separator;
    }

    /**
     * 
     * @param pf
     */
    protected void setPreFetch(String pf){
        this.preFetch = pf;
    }

    /**
     * 
     * @return
     */
    public String getPreFetch() {
        return preFetch;
    }
}