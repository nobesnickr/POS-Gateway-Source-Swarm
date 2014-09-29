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
package com.sonrisa.swarm.posintegration.warehouse;

import java.sql.Timestamp;

/**
 * Filter from the DataWarehouse to be used to filter remote data
 * @author sonrisa
 *
 */
public class DWFilter implements Comparable<DWFilter> {

    /**
     * Timestamp of the filter
     */
    private Timestamp time = new Timestamp(0L);
    
    /**
     * Id of the filter
     */
    private long id = 0L;
    
    /**
     * Initilize using the timestamp of the filter
     * @param time
     * @return
     */
    public static DWFilter fromTimestamp(Timestamp time){
        DWFilter retval = new DWFilter();
        retval.setTime(time);
        return retval;
    }
    
    /**
     * Initilize using the id of the filter
     * @param id
     * @return
     */
    public static DWFilter fromId(long id){
        DWFilter retval = new DWFilter();
        retval.setId(id);
        return retval;
    }

    /**
     * Compares DWFilter to an other, by comparing
     * the wrapper Timestamp intstances
     * 
     * @param filter Filter to be comparing against
     */
    @Override
    public int compareTo(DWFilter filter) {
        int value = time.compareTo(filter.time);
        if(value == 0){
            value = new Long(id).compareTo(filter.id);
        }
        return value;
    }

    public Timestamp getTimestamp() {
        return time;
    }

    public long getId() {
        return id;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setId(long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        DWFilter other = (DWFilter) obj;
        if (id != other.id){
            return false;
        }
        if (time == null) {
            if (other.time != null){
                return false;
            }
        } else if (!time.equals(other.time)){
            return false;
        }
        return true;
    }
}
