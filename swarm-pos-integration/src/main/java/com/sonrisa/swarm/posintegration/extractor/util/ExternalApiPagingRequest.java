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
package com.sonrisa.swarm.posintegration.extractor.util;

import java.util.Iterator;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;

/**
 * In most cases the remote an external API maximizes the number of records
 * returned in one API request. This class hides this behavior, and 
 * allows the Extractor to iterate through all records for a request
 * using this iterator.
 * @param <T> The items iterating over, e.g. JsonNode
 */
@Deprecated
public abstract class ExternalApiPagingRequest<T> implements Iterator<T>, Iterable<T>  {
    /** Results are fetched in pages, each with e.g. 100 records, this shows the 0..99 index within the page*/
    private int indexWithinPage = 0;
    /** If records exceed the limit of fetchSize, they can be requested with specifying a page index */
    private int currentPage;
    /** The total number of records, -1 means that this value is yet unknown */
    private int recordCount = -1;
    
    /** Number of records fetched during on API call */
    public static final int DEFAULT_FETCH_SIZE = 100;

    /** The number of records on one page */
    protected int fetchSize = DEFAULT_FETCH_SIZE;
    
    /** Fetch the first page of data, and reset all counters to 0 */
    private void firstFetch() throws ExternalExtractorException {
        currentPage = 0;
        indexWithinPage = 0;
        fetchPage(currentPage);
    }

    /** Fetch item from current page using the page index. For example: currentRecords.get(indexWithinPage) */
    protected abstract T getItemFromCurrentPage(int i);

    /** Fetch data from the remote system */
    protected abstract void fetchPage(int page) throws ExternalExtractorException;

    /** Returns true if there is a next element to be processed */
    @Override
    public boolean hasNext() {
      //if recordCount is yet unknown retrieve the first page, status information will carry the recordCount information
        if(recordCount < 0) {
            try {
                firstFetch();
            } catch (ExternalExtractorException e){
                throw new ExternalPageIterationException(e);
            }
        }
        
        //if the total number of elements processed is less then the total number records, then there is one more element
        if(currentPage * fetchSize + indexWithinPage < recordCount) {
            return true;
        }
        return false;
    }
    
    /** 
     * Returns the next JsonNode from the records array that hasn't been processed 
     * @returns Null of no more elements, the next element in line otherwise
     * */
    @Override
    public T next() {
        try {
            // First time call
            if(recordCount < 0) {
                firstFetch();
            }
            
            if(!hasNext()) {
                return null;
            }
            
            // Still items in the current page
            if(indexWithinPage < fetchSize) {
                return this.getItemFromCurrentPage(indexWithinPage++);
            }
            
            // If reached the end of the page
            ++currentPage;
            indexWithinPage = 0;
            fetchPage(currentPage);
            
            // Return the 0 item from the next page and iterate
            return this.getItemFromCurrentPage(indexWithinPage++);
        } catch (ExternalExtractorException ex){
            throw new ExternalPageIterationException(ex);
        }
    }

    /** Deprecated, as pager provides only readonly access */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param recordCount the recordCount to set
     */
    protected void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    
    /** Returns the total number of records returned by the API response */
    public int getRecordCount(){
        return this.recordCount;
    }

    /**
     * @return the fetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * @param fetchSize the fetchSize to set
     */
    protected void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * This class server as Iterator and Iterable
     */
    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
