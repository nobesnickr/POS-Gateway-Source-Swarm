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

package hu.sonrisa.backend.exception;

/**
 * Runtime exception-ök amik jöhetnek a utility osztályokból
 * @author kelemen
 */
public class BackendException extends RuntimeException {

    /**
     * Becsomagolandó exception
     * @param cause
     */
    public BackendException(Throwable cause) {
        super(cause);
    }

    /**
     * Becsomagolandó exc és specifikus üzenet
     * @param message
     * @param cause
     */
    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param message
     */
    public BackendException(String message) {
        super(message);
    }

    /**
     *
     */
    public BackendException() {
    }
}
