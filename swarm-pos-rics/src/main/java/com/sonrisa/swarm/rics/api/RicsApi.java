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

import org.apache.http.ParseException;
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
import com.sonrisa.swarm.rics.constants.RicsUri;

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
	 * Retrieves the authorization token that used to access to RICS
	 * @param account the account would like to access to the service
	 * @return 
	 * @throws ExternalExtractorException
	 */
	private String getAuthorizationToken(RicsAccount account) throws ExternalExtractorException {
		if (!StringUtils.hasLength(account.getLastToken())) {
			renewToken(account);
		}
		return account.getLastToken();
	}

	/**
	 * sends a request for a new authentication token for RICS services and sets it to the account for further use
	 * @throws ExternalExtractorException 
	 */
	private void renewToken(RicsAccount account) throws ExternalExtractorException {
		Map<String, String> jsonData = new HashMap<String, String>();
		jsonData.put("SerialNumber", account.getSerialNum());
		jsonData.put("Login", account.getLoginName());
		jsonData.put("Password", account.getPassword());

		try {

			ExternalDTO responseBody = executeCommand(new ExternalCommand<RicsAccount>(account, RicsUri.LOGIN.uri), jsonData).getContent();
			account.setToken(responseBody.getText("Token"));
		} catch (ParseException e) {
			throw new ExternalExtractorException("Unable to parse response", e);
		}
	}

	/**
	 * Send request to RICS webservice using the account set
	 * 
	 * @param command contains the data required to fire the request
	 * @returns {@link ExternalDTO} wrapped response value
	 */
	@Override
	public ExternalResponse sendRequest(ExternalCommand<RicsAccount> command) throws ExternalExtractorException {
		RicsAccount acc = command.getAccount();

		Map<String, String> params = new HashMap<String, String>(command.getParams());
		params.put("Token", getAuthorizationToken(acc)); // add authorization data

		try {
			return executeCommand(command, params);
		} catch (ExternalApiBadCredentialsException e) {
			LOGGER.debug("Request failed (maybe old token)", e);
			renewToken(acc);
			return executeCommand(command, params);
		}

	}

	private ExternalResponse executeCommand(ExternalCommand<RicsAccount> command, Map<String, String> params) throws ExternalExtractorException {
		String url = apiBaseUrl + command.getURI();
		HttpPost request = new HttpPost(url);

		StringEntity json;
		try {
		    final String jsonString = MAPPER.writeValueAsString(params);
			json = new StringEntity(jsonString);
			json.setContentType(MediaType.APPLICATION_JSON_VALUE);
			request.setEntity(json);
			
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
