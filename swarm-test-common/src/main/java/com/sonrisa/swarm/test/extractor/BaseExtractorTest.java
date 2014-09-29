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

package com.sonrisa.swarm.test.extractor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TimeZone;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.test.matcher.ExternalCommandMatcher;

/**
 * Base class for testing {@link ExternalExtractor} implementations
 */
public abstract class BaseExtractorTest<T extends SwarmStore> extends BaseWarehouseTest {
    
    /**
     * Mocked API, data source
     */
    @Mock
    protected ExternalAPI<T> api;

    /**
     * Assert that ISO8061 filter was added to the REST query
     * 
     * @param filterKey
     *            E.g. <code>updated_at_min</code>
     * @param queryUris
     *            REST URIs where this is expected, e.g. Invoices
     */
    protected void assertISO8061TimeFilter(String filterKey, final String... queryUris) {
        final String expected = ISO8061DateTimeConverter.dateToMysqlString(filter.getTimestamp());
        assertContainsParams(filterKey, expected, queryUris);
    }

    /**
     * Assert that remote id filter was added to the REST query
     * 
     * @param filterKey
     *            E.g. <code>id</code>
     * @param queryUris
     *            REST URIs where this is expected, e.g. Customers
     */
    protected void assertRemoteIdFilter(String filterKey, final String... queryUris) {
        final String expected = Long.toString(filter.getId());
        assertContainsParams(filterKey, expected, queryUris);
    }

    /**
     * Assert that a certain parameter was added to the REST query
     * 
     * @param filterKey
     *            E.g. <code>order_by</code>
     * @param queryUris
     *            REST URIs where this is expected, e.g. Invoices
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void assertContainsParams(String fieldKey, String expectedFieldValue, final String... queryUris) {

        ArgumentCaptor<ExternalCommand> captor = ArgumentCaptor.forClass(ExternalCommand.class);

        try {
            verify(api, atLeast(queryUris.length)).sendRequest(captor.capture());
        } catch (ExternalExtractorException e) {
            throw new RuntimeException("Verification failed.", e);
        }

        HashSet<String> verificationTargets = new HashSet<String>(Arrays.asList(queryUris));

        for (int i = 0; i < captor.getAllValues().size(); i++) {
            ExternalCommand command = captor.getAllValues().get(i);
            if (verificationTargets.contains(command.getURI())) {
                assertTrue(fieldKey + " missing for " + command.getUrlQueryString(),
                        command.getParams().containsKey(fieldKey));
                assertEquals("Field not matching for " + command.getUrlQueryString(), expectedFieldValue, command
                        .getParams().get(fieldKey));
            }

        }
    }

    /**
     * Add new REST service endpoint to the mock API, which will return a MOCK JSON object
     * 
     * @param matches Matcher matching that the REST service was invoked
     * @param resourceUri Project Resource URI in <i>swarm-test-common</i> to be returned, should be JSON 
     */
    protected void addJsonRestService(ExternalCommandMatcher<T> matches, String resourceUri) {
        try {
            when(api.sendRequest(argThat(matches))).thenReturn(MockDataUtil.getResourceAsExternalResponse(resourceUri));
        } catch (ExternalExtractorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the first item from a resource json file
     * 
     * @param resourcePath
     * @return
     */
    protected JsonNode firstMockJson(String resourcePath, String dataKey) {
        return MockDataUtil.getResourceAsJson(resourcePath).get(dataKey).get(0);
    }
    
    /**
     * Format Date as in LsPro jsons
     */
    protected static String getLsProDate(Timestamp date){
        return dateToAssertionString(date, "yyyy-MM-dd'T'HH:mm:ss.SSS");
    }
    
    /**
     * Format Date as in LsPro jsons
     */
    protected static String getKountaDate(Timestamp date, TimeZone timezone){
        SimpleDateFormat lsProDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX:00");
        lsProDateFormat.setTimeZone(timezone);
        return lsProDateFormat.format(date);
    }

    /**
     * Format Date as in LsPro jsons
     */
    protected static String dateToAssertionString(Timestamp date, String format){
        SimpleDateFormat lsProDateFormat = new SimpleDateFormat(format);
        return lsProDateFormat.format(date);
    }
}
