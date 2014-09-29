/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.rics.util;

/**
 * RICS could not supply unique id for its invoices.
 * This class is used to generate id for invoices and invoice lines
 * We would like to get an id thats binary representation follows some rules:<br>
 * - positive long
 * - contains the low 32 bit of the rendundantId in its 32 high bit
 * - the next 16 bit is the invoiceId
 * - the last 15 bit is the invoiceLineId
 * @author Sonrisa
 *
 */
public class RicsIdGenerator {
	
	/**
	 * Generates a 64 bit long id for invoices 
	 * In the generated id the first (left side) bit is the sign, that must be 0 (positive).
	 * @param redundantId a unique or very rare number (creation time of invoice) Its low (right side) 32 bit will be the result's top 32 bit
	 * @param salt this will be binary concatenated to the redundantId (16 bit)
	 * @return concatenation of [redundantId low 32 bit][salt]'000000000000000'
	 */
	public static long generateInvoiceId(long redundantId, short salt) {
		if (redundantId<0 || salt<0) {
			throw new IllegalArgumentException("Neither parameter can be negative");
		}
		int low = (int) (redundantId & 0xFFFFFFFFL); //the low (right handed) 32 bits
		//concatenate the binary representation of the 
		Long result = (long)low << 31 | salt << 15  & 0xFFFFFFFFL; 
		result &= (1L << 63) - 1; // be positive :) => 32 bit redundantId + 16 bit salt + 15 bit zeros 
		return result;
	}
	
	/**
	 * Generates a 64 bit long id for invoiceLine.
	 * 
	 * Warning!!!: the low 15 bit of invoiceId will be dropped and replaced with the salt
	 * 
	 * @param invoiceId the invoice's id that this line belongs to. It should be generated with {@link #generateInvoiceId(long, short)}
	 * @param salt this will be binary concatenated to the redundantId low 16 bit
	 * @param salt2 this will be the lowest 15 bit of the result
	 * @return the modified invoiceId: its low 15 bit (zeros) replaced by the salt
	 */
	public static long generateLineId(long invoiceId, short salt) {
		if (invoiceId<0 || salt<0) {
			throw new IllegalArgumentException("Neither parameter can be negative");
		}
		//concatenate the binary representation of invoiceId and salt
		Long result = (long)invoiceId & 0xFFFFFFFFFFFF8000L | salt  & 0xFFFFFFFFL; 
		return result;
	}
}
