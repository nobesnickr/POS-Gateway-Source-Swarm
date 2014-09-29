/*
 * Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sonrisa
 * Informatikai Kft. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Sonrisa.
 * 
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.sonrisa.swarm.retailpro.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the retailPro specific file operations
 */
public class FileUtils {
	
	private static final Logger	DEFAULT_LOGGER	= LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * Attempts to create directory if doesn't exist
	 * 
	 * @param path the path of the directory
	 */
	public static void createDirectoryIfDoesntExist(String path) {
		File directory = new File(path);
		if ( !directory.exists() ) {
			DEFAULT_LOGGER.warn("Client log directory doesn't exists: {}", path);
			boolean created = directory.mkdirs();
			if ( !created ) {
				DEFAULT_LOGGER.error("Failed to created log directory to path: {}", path);
				throw new SecurityException("Failed to create client upload directory");
			}
		}
	}
}
