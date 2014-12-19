/*
  Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.sonrisa.swarm.rics.api;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.util.BaseRestAPI;
import com.sonrisa.swarm.posintegration.api.util.impl.SimpleRestVerifier;
import com.sonrisa.swarm.posintegration.exception.ExternalApiBadCredentialsException;
import com.sonrisa.swarm.posintegration.exception.ExternalApiException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.rics.RicsAccount;

/**
 * Tool to fetch data from  RICS's REST service.
 * @author Sonrisa
 *
 */
@Component("ricsApi")
public class RicsApi extends BaseRestAPI implements ExternalAPI<RicsAccount> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RicsApi.class);

	/**
	 * Serializes / deserializes JSON data 
	 */
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * Maximum date for RICS
	 */
	public static final String DATE_MAX = "1/1/9999";
	
	/**
	 * Minimum date for RICS
	 */
	public static final String DATE_MIN = "1/1/1753";

	/**
	 * URL to access the RICS web service
	 */
    @Value("${rics.api.rest.uri.base}") 
    private String apiBaseUrl = "http://localhost/";
	
    
	public RicsApi() {
		super();
        // Set JSON response verifier for inspecting odata.error
        setRestVerifiers(
                SimpleRestVerifier.forPathEquals(
                        ExternalApiBadCredentialsException.class,
                        "$.IsSuccessful",
                        "false")
                        .withMetaField("Inner-Error", "$.Message")
                        .withMetaField("Validation-Errors", "$.ValidationMessages[*]"),
                SimpleRestVerifier.forOkStatus(ExternalApiException.class));
	}

	/**
	 * Send request to RICS webservice using the account set
	 * 
	 * @param command contains the data required to fire the request
	 * @returns {@link ExternalDTO} wrapped response value
	 */
	@Override
	public ExternalResponse sendRequest(ExternalCommand<RicsAccount> command) throws ExternalExtractorException {
		Map<String, String> params = new HashMap<String, String>(command.getParams());
		return executeCommand(command, params);
	}

	private ExternalResponse executeCommand(ExternalCommand<RicsAccount> command, Map<String, String> params) throws ExternalExtractorException {
		
		final RicsAccount account = command.getAccount();
		final String url = apiBaseUrl + command.getURI();
		HttpPost request = new HttpPost(url);

		StringEntity json;
		try {
			
			// Prepare payload
		    final String jsonString = MAPPER.writeValueAsString(params);
			json = new StringEntity(jsonString);
			json.setContentType(MediaType.APPLICATION_JSON_VALUE);
			request.setEntity(json);
			
			// Prepare headers
			if(StringUtils.isEmpty(account.getToken())){
				throw new ExternalExtractorException("No token provided for RICS");
			}
			
			request.addHeader("Token", account.getToken());
			
			LOGGER.debug("Request content: {}", jsonString);
			return executeRequest(request);
		} catch (UnsupportedEncodingException e) {
			throw new ExternalExtractorException(e);
		} catch (JsonProcessingException e) {
			throw new ExternalExtractorException(e);
		}
	}

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}
