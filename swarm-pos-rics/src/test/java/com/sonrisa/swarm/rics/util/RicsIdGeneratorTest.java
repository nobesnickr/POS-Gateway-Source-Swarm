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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class RicsIdGeneratorTest {

	/**
	 * Test case:
	 * generate two (one invoice and one invoiceLine) id from 3 well known number and check it with the desired 32-16-15 pattern.
	 * 
	 * Expected:
	 * The pattern and the generated id equals
	 */
	@Test
	public void testAgainstPattern() {
		long ones = Long.MAX_VALUE;
		short zeros = 0;
		short changing = Short.valueOf("21845");// 101010101010101

		long invoiceId;

		// invoice
		String invoicePattern = "111111111111111111111111111111110000000000000000000000000000000";
		invoiceId = RicsIdGenerator.generateInvoiceId(ones, zeros);
		assertEquals(invoicePattern, Long.toBinaryString(invoiceId));

		// invoiceLine
		String linePattern = "111111111111111111111111111111110000000000000000101010101010101";
		assertEquals(new BigInteger(linePattern, 2).longValue(),
				RicsIdGenerator.generateLineId(invoiceId, changing));
	}

	/**
	 * Test case: 
	 * generate two (one invoice and one invoiceLine) id from random numbers
	 * 
	 * Expected:
	 * the generated id equals to the builded binary strings' value
	 */
	@Test
	public void testRandom() {
		long time = System.currentTimeMillis();
		short salt1 = (short) (Math.random() * Short.MAX_VALUE + 1);
		short salt2 = (short) (Math.random() * Short.MAX_VALUE + 1);

		// generate the result with String manipulation
		String timeAsBinary = Long.toBinaryString(time);
		String shortTime = timeAsBinary.substring(timeAsBinary.length() - 32);

		String salt1AsBinary = Long.toBinaryString(salt1);
		// fill the binary with '0' placeholders
		String filledSalt1 = addTrailingZeros(salt1AsBinary, 16);

		String salt2AsBinary = Long.toBinaryString(salt2);
		// fill the binary with zero placeholders
		String filledSalt2 = addTrailingZeros(salt2AsBinary, 15);

		// invoice
		long invoiceId = RicsIdGenerator.generateInvoiceId(time, salt1);

		StringBuilder invoiceBinaryBuilder = new StringBuilder(shortTime);
		invoiceBinaryBuilder.append(filledSalt1);
		// append low zero bits (invoiceLineId placeholder)
		invoiceBinaryBuilder.append("000000000000000");
		assertEquals(new BigInteger(invoiceBinaryBuilder.toString(), 2).longValue(), invoiceId);

		// invoiceLine
		long lineId = RicsIdGenerator.generateLineId(invoiceId, salt2);

		StringBuilder invoiceLineBinaryBuilder = new StringBuilder(shortTime);
		invoiceLineBinaryBuilder.append(filledSalt1);
		invoiceLineBinaryBuilder.append(filledSalt2);
		assertEquals(new BigInteger(invoiceLineBinaryBuilder.toString(), 2).longValue(), lineId);
	}

	/**
	 * adds as many trailing zeros to num as required to get the desired number of didgits
	 * @param num the string representation of a number
	 * @param digitNum the expected number of digits of num 
	 * @return num with the trailing zeros
	 */
	private String addTrailingZeros(String num, int digitNum) {
		String leadingZeros = StringUtils.repeat('0', digitNum-num.length());
		return leadingZeros.concat(num);
	}

	/**
	 * Test case:
	 * generate two invoice and two invoiceLine id with the same parameters
	 * 
	 * Expected:
	 * the same id generated for the same parameters
	 */
	@Test
	public void testDeterministic() {
		long time = System.currentTimeMillis();
		short salt1 = (short) (Math.random() * Short.MAX_VALUE + 1);
		short salt2 = (short) (Math.random() * Short.MAX_VALUE + 1);

		// invoice
		long invoiceid = RicsIdGenerator.generateInvoiceId(time, salt1);
		assertEquals(invoiceid, RicsIdGenerator.generateInvoiceId(time, salt1));

		// invoiceLine
		assertEquals(RicsIdGenerator.generateInvoiceId(invoiceid, salt2), RicsIdGenerator.generateInvoiceId(invoiceid, salt2));
	}
}
