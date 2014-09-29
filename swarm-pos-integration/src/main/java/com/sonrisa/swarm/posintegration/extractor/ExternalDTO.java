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
package com.sonrisa.swarm.posintegration.extractor;

import java.sql.Timestamp;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;


/**
 * Data transfer object sent by a foreign system, similar to a Map<String,Object>
 */
public interface ExternalDTO {
    
    /**
     * Get numeric value from the DTO identified by a key
     * @param key Key that identifies the value, e.g. "taxAmount"
     * @return The value of the field identified by the key
     * @throws ExternalExtractorException if value can't be extracted
     */
    public int getInt(String key) throws ExternalExtractorException;
   
    /**
     * Get numeric value from the DTO identified by a key
     * @param key Key that identifies the value, e.g. "taxAmount"
     * @return The value of the field identified by the key
     * @throws ExternalExtractorException if value can't be extracted
     */
    public long getLong(String key) throws ExternalExtractorException;
    
    /**
     * Get numeric value from the DTO identified by a key
     * @param key Key that identifies the value, e.g. "taxAmount"
     * @return The value of the field identified by the key
     * @throws ExternalExtractorException if value can't be extracted
     */
    public double getDouble(String key) throws ExternalExtractorException;
    
    /**
     * Get numeric value from the DTO identified by a key as short
     * @param key Key that identifies the value, e.g. "taxAmount"
     * @return The value of the field identified by the key
     * @throws ExternalExtractorException if value can't be extracted
     */
    public short getShort(String key) throws ExternalExtractorException;
    
    /**
     * Get text value from the DTO identified by a key
     * @param key Key that identifies the value, e.g. "firstName"
     * @return The value of the field identified by the key or an empty String if value extraction fails
     */
    public String getText(String key);
    
	/**
	 * converts and returns the value of a node identified by a key.
	 * The conversion is made by the rules of ISO-8061 
	 * @return the {@link Timestamp} converted from the value
	 * @throws ExternalExtractorException if no node found with the given key
	 */
	public Timestamp getTimeStampISO8061(String key) throws ExternalExtractorException;
    
    /**
     * Get iterator for nested objects, e.g. invoice lines in invoice
     * @param key The key identifying the nested objects, e.g. "rows"
     * @return Iterable object
     * @throws ExternalExtractorException
     */
    public Iterable<ExternalDTO> getNestedItems(String key) throws ExternalExtractorException;
    
    /**
     * Get number of nested objects
     * @param key Key of the nested array
     * @return Number of items in nested items or <i>zero</i> if non-existing or empty
     */
    public int getNestedItemSize(ExternalDTOPath key);
    
    /**
     * Get nested object
     * @param key The key identifying the nested objects, e.g. "Contact"
     * @return The object that was nested
     * @throws ExternalExtractorException
     */
    public ExternalDTO getNestedItem(ExternalDTOPath path) throws ExternalExtractorException;
    
    /**
     * Get nested object
     * @param index The index identifying the nested objects, e.g. "Contact"
     * @return The object that was nested
     * @throws ExternalExtractorException
     */
    public ExternalDTO getNestedArrayItem(int index) throws ExternalExtractorException;
    
    /**
     * Indicates whether a certian field exists
     * @param key Key identifing the field
     * @return True if the field exists
     */
    public boolean hasKey(String key);
}
