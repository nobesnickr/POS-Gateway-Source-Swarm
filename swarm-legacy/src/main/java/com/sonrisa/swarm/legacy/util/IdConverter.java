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
package com.sonrisa.swarm.legacy.util;

/**
 * Swarm does not like negative identifiers, but retailPro clients often sends us customer identifiers < 0
 * This class converts these ids to be > 0
 * @author Sonrisa
 *
 */
public final class IdConverter {

	/**
	 * Retrieves the identifier's absolute value. 
	 * Very likely unique, but not sure
	 * @param id the id to convert
	 * @return the absolute value
	 */
	public static Long positiveCustomerId(long id) {
		return Math.abs(id);
	}

	/**
	 * Retrieves the identifier's absolute value. 
	 * Very likely unique, but not sure
	 * @param id the identifier number as String
	 * @return the absolute value
	 * @throws NumberFormatException if parameter is not parseable as long
	 */
	public static Long positiveCustomerId(String idNum) {
		return positiveCustomerId(Long.valueOf(idNum));
	}

	// this is a util class
	private IdConverter() {
	}

}
