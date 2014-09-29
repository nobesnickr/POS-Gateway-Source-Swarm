/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.posintegration.warehouse.csvdump;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Implementation of Swarm Store that dumps data into 
 * a CSV file
 */
@Service
public class CsvDumpDTOService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvDumpDTOService.class);
    
    private PrintWriter writer = null;
    private String currentFileName = null;
    
    private static String folderCache = null;
    
    /**
     * Returns the folder to write to (with OS appropriate slash or backslash at the end)
     * @return
     */
    protected static String getDestinationDirectory(){
        if(folderCache == null){
            folderCache = System.getProperty("user.home") + File.separator + "swarm" + File.separator + "dump" + File.separator;
        }
        return folderCache;
    }
    
    /**
     * Returns the name of the file it should be saved as
     * @return
     */
    protected static String getFileFromDate(){
        return "dump_" + (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + ".csv";
    }
    
    /**
     * Adds slashes to string so it can be included in a CSV file
     * @see http://dansesacrale.wordpress.com/2010/10/01/java-custom-addslashes/
     * @return
     */
    public static String addSlashes(final String unformattedString) {
        String s = unformattedString.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\\n", "\\\\n");
        s = s.replaceAll("\\r", "\\\\r");
        s = s.replaceAll("\\00", "\\\\0");
        s = s.replaceAll("'", "\\\\'");
        return s;
    }
    
    /**
     * If directory doesn't exist create it
     */
    private void prepareDirectory(){
        //create directory if doesn't exist
        File dir = new File(getDestinationDirectory());
        if(!dir.exists()){
            if(!dir.mkdir()){
                LOGGER.error("Failed to created directory: {}",  dir.getAbsolutePath());
            } else {
                LOGGER.debug("Created directory: {}", dir.getAbsolutePath());
            }
        }
    }

    /**
     * Appends str to the file
     */
    private void saveToFile(final Object... fields){
        String fileName = getFileFromDate();

        try {
            // if new day
            if(writer == null || !fileName.equals(currentFileName)){
                if(writer != null){
                    close();
                }
               
                // if directory doesn't exist create it
                prepareDirectory();
                
                currentFileName = fileName;
                final String fileLocation = getDestinationDirectory() + currentFileName;
                writer = new PrintWriter(
                            new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(fileLocation, true),"UTF-8") 
                            )
                         );
            }
            
            // write content to file
            boolean first = true;
            for(Object object : fields){
                String str = object == null ? "NULL" : object.toString();
                
                // if weird characters we 
                if(str.contains("\"") || str.contains("\\") || str.contains("'") || str.contains("\r")){
                    str = addSlashes(str);
                } 
                writer.print((first ? "" : ",") + "'" + str + "'" );
                first = false;
            }
            writer.println();
        } catch (IOException e) {
            LOGGER.error("Failed to write to file: {}", currentFileName, e);
            writer = null;
            currentFileName = null;
        }        
    }
    
    /**
     * Flush data to the file
     */
    public void flush() {
        if(writer != null){
            writer.flush();
        }
    }
    
    /**
     * Close file writer
     */
    public void close() throws IOException{
        if(writer != null){
            writer.close();
            writer = null;
            LOGGER.info("Closing Dump");
        }
    }

    public void saveCategories(SwarmStore store, List<CategoryDTO> categories) {
        for(CategoryDTO c : categories) {
            saveToFile("CATEGORY", store.getStoreId(), c.getRemoteId(), c.getCategoryName(), c.getParentCategory(), c.getLastModified());
        }
        flush();
    }

    public void saveCustomers(SwarmStore store, List<CustomerDTO> customers) {
        for(CustomerDTO c : customers) {
            saveToFile("CUSTOMER", store.getStoreId(), c.getRemoteId(), c.getName(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getPhoneNumber(), 
            c.getAddress(), c.getAddress2(), c.getCity(), c.getState(), c.getCountry(), c.getPostalCode(), c.getNotes(), c.getLastModified());
        }
        flush();
    }

    public void saveInvoices(SwarmStore store, List<InvoiceDTO> invoices) {
        for(InvoiceDTO i : invoices){
            saveToFile("INVOICE", store.getStoreId(), i.getRemoteId(), i.getInvoiceNumber(), i.getCustomerId(), i.getTotal(), i.getLastModified());
        }
        flush();
    }

    public void saveInvoiceLines(SwarmStore store, List<InvoiceLineDTO> invoiceLines) {
        for(InvoiceLineDTO il : invoiceLines){
            saveToFile("INVOICE_LINE", store.getStoreId(), il.getInvoiceId(), il.getRemoteId(), il.getPrice(), il.getTax(), il.getProductId(), il.getQuantity(), il.getLastModified());
        }
        flush();
    }

    public void saveManufacturers(SwarmStore store, List<ManufacturerDTO> manufacturers) {
        for(ManufacturerDTO m : manufacturers){
            saveToFile("MANUFACTURER", store.getStoreId(), m.getRemoteId(), m.getManufacturerName(), m.getLastModified());
        }
        flush();
    }

    public void saveProducts(SwarmStore store, List<ProductDTO> products) {
        for(ProductDTO p : products){
            saveToFile("PRODUCT", store.getStoreId(), p.getRemoteId(), p.getManufacturerId(), p.getManufacturerName(), p.getCategoryId(), p.getCategoryName(),
                       p.getEan(), p.getUpc(), p.getStoreSku(), p.getPrice(), p.getDescription(), p.getLastModified()
                      );            
        }
        flush();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends DWTransferable> void saveToDump(SwarmStore store, List<? extends T> entities, Class<T> clazz) {
        if(clazz == InvoiceDTO.class){
            this.saveInvoices(store, (List<InvoiceDTO>) entities);
        } else if(clazz == InvoiceLineDTO.class){
            this.saveInvoiceLines(store, (List<InvoiceLineDTO>) entities);
        } else if(clazz == ManufacturerDTO.class){
            this.saveManufacturers(store, (List<ManufacturerDTO>) entities);
        } else if(clazz == CustomerDTO.class){
            this.saveCustomers(store, (List<CustomerDTO>) entities);
        } else if(clazz == ProductDTO.class){
            this.saveProducts(store, (List<ProductDTO>) entities);
        } else if(clazz == CategoryDTO.class){
            this.saveCategories(store, (List<CategoryDTO>) entities);
        }
    }

    @Override
    public String toString() {
        return "CsvDumpDataStore [currentFileName=" + currentFileName + "]";
    }
}
