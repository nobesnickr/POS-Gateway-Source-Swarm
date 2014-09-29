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

package com.sonrisa.swarm.posintegration.api.reader.impl;

import java.util.HashMap;
import java.util.Map;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

/**
 * There are POS systems that are respond JSONs with different data keys for different requests. 
 * This resolver can be used in situations like this.
 * The client code should provide a {@code Map} that maps the request URIs to the data key used in the response JSON.
 *
 * @param <T>
 */
public class URIBasedDataKeyResolver<T extends SwarmStore> implements ExternalDataKeyResolver<T> {
	
	private Map<String, String> uriDataKeyMap;
	
	/**
	 * Creates a new resolver that uses a {@code Map} to determine which data key should be used to parse the response
	 * @param uriDataKeyMap the {@code Map} that contains the mapping between the request URIs to data keys in response
	 */
    public URIBasedDataKeyResolver(Map<String, String> uriDataKeyMap) {
		super();
		this.uriDataKeyMap = uriDataKeyMap;
	}
    
	public URIBasedDataKeyResolver() {
		this(new HashMap<String, String>());
	}

	/**
	 * What is the root json key for a request?
	 * @param request E.g. products.json or Invoices
	 * @return JSON key, e.g. products or Invoices
	 */
	public static String getDefaultDataKey(String request) {
		if (request.indexOf('.') > 0) {
			return request.substring(0, request.indexOf('.'));
		} else {
			return request;
		}
	}

	/**
	 * Returns the data key mapped to the command's URI
	 * @param command the command that will be executed by the extractor
	 * @return the data key, that identifies the root element of the significant part in the responded JSON
	 */
	@Override
	public ExternalDTOPath getDataKey(ExternalCommand<T> command) {
		if (uriDataKeyMap.containsKey(command.getURI())) {
			return new ExternalDTOPath(uriDataKeyMap.get(command.getURI()));
		}
		return new ExternalDTOPath(getDefaultDataKey(command.getURI()));
	}
}
