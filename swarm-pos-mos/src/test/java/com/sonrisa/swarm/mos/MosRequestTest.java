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
package com.sonrisa.swarm.mos;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonrisa.swarm.posintegration.exception.ExternalDeniedServiceException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;

/** 
 * Testing the iteration behaviour of the MosRequest class, it's 
 * ability to backoff if service is denied, etc. 
 * 
 */
public class MosRequestTest {
    
    /**
     * Not tested
     */
    @Mock
    private MosAccount account;

    /**
     * Utility tool to generate JSON with enough information for 
     * the iterator to function properly
     * @param key The key of the request for Mos, e.g. Item
     * @param pageNo The pageNo of the response
     * @param limit The number of elements to be returned (e.g. 100 or 45)
     * @param total The total number of elements (e.g. 445)
     * @return JsonNode 
     */
    private static JsonNode getMockPage(String key, int pageNo, int limit, int total){
        
        StringBuilder firstPage = new StringBuilder();
        for(int i = 0; i < limit; i++){
            if(firstPage.length() != 0){
                firstPage.append(",");
            }
            firstPage.append(String.format(Locale.ENGLISH,"{\"upc\":\"100000%d\",\"price\":%.4f}", pageNo * 1000 + i, 100.0 * (i + limit/2) / (limit*2)));
        }
        
        // Wrap to Merchant OS format
        String text = "{\"@attributes\":{\"count\":" + total + "},\"" + key + "\":[" + firstPage.toString() + "]}";
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            //Return the json
            return mapper.readValue(text, JsonNode.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Test that all kinds of data is requested from the api
     */
    @Test
    public void testNumberOfItemsRead() throws ExternalExtractorException{
        // Number of total items to be mocked
        final int count = 345;
         
        MosAPI api = new MosAPI(){
            
            /**
             * Mocking sending request
             */
            @Override
            public JsonNode sendRequest(MosAccount account, String queryUrl) throws ExternalExtractorException {
                int offset = 0;
                int limit = 100;
                
                if(!queryUrl.startsWith("Item")){
                    throw new ExternalExtractorException("Invalid request");
                }
                
                queryUrl = queryUrl.substring("Item?".length());
                
                Scanner scanner = new Scanner(queryUrl);
                scanner.useDelimiter("[&=]");
                
                while(scanner.hasNext()){
                    String key = scanner.next();
                    if("offset".equals(key)){
                        offset = scanner.nextInt();
                    } 
                    else if("limit".equals(key)){
                        limit = scanner.nextInt();
                    } else {
                        scanner.close();
                        throw new ExternalExtractorException();
                    }
                }
                
                scanner.close();
                int page = offset / limit + (offset % limit == 0 ? 0 : 1);
                return getMockPage("Item", page, Math.min(limit, count - offset),count);
            }
        };
        
        MosRequest request = new MosRequest(api, account, "Item", new HashMap<String,String>());
        
        HashSet<Integer> unique = new HashSet<Integer>();
        
        int elementCount = 0;
        for(ExternalDTO node : request){
            unique.add(Integer.parseInt(node.getText("upc")));
            elementCount++;
        }
        
        // Iteration count should match element count
        assertEquals(count, elementCount);
        assertEquals(count, unique.size());      
    }
    
    /**
     * Test that denying service of the iterator
     * doens't halt the foreach, but forces the 
     * request handler to retry
     * @throws ExternalExtractorException 
     */
    @Test
    public void testDenielTwiceDoesntFail() throws ExternalExtractorException{
        final int rowCount = 42;
        
        MosAPI api = new MosAPI() {
            private int count = 0;
            
            /** 
             * Send request throws denial exception twice, 
             * then returns the correct data
             */
            @Override
            public JsonNode sendRequest(MosAccount account, String queryUrl) throws ExternalExtractorException {
                if(queryUrl.startsWith("Item")){
                    if(count++ < 2){
                        throw new ExternalDeniedServiceException();
                    }
                    return getMockPage("Item", 0, rowCount, rowCount);
                }
                throw new ExternalExtractorException("Unknown");
            }
        };
        
        MosRequest request = new MosRequest(api, account, "Item", new HashMap<String, String>()){
            /** Use 1 ms not to hold back unit test execution */
            @Override
            public int getInitialBackoffMilliseconds() {
                  return 1;
            }  
        };
        
        int iterationCount = 0;
        for(ExternalDTO node : request){
            iterationCount++;
        }

        assertEquals(rowCount, iterationCount); 
    }
    
    /**
     * Test that denying service of the iterator
     * halts if denied too many times
     * @throws ExternalPageIterationException 
     */
    @Test(expected = ExternalPageIterationException.class)
    public void testDenielManyTimesDoesntFail() throws ExternalExtractorException{
        final int rowCount = 42;
        
        MosAPI api = new MosAPI() {
            private int count = 0;
            
            /** Throws denial of service one more times than supposed to */
            @Override
            public JsonNode sendRequest(MosAccount account, String queryUrl) throws ExternalExtractorException {
                if(queryUrl.startsWith("Item")){
                    if(count++ < MosRequest.EXPONENTIAL_BACKOFF_LIMIT + 1){
                        throw new ExternalDeniedServiceException();
                    }
                    return getMockPage("Item", 0, rowCount, rowCount);
                }
                throw new ExternalExtractorException("Unknown");
            }
        };
        
        MosRequest request = new MosRequest(api, account, "Item", new HashMap<String, String>()){
            /** Use 1 ms not to hold back unit test execution */
            @Override
            public int getInitialBackoffMilliseconds() {
                  return 1;
            }  
        };
        
        // Act
        request.next(); 
    }
    
    @Ignore
    @Test
    public void testExecute() throws ParseException{
        final String apiKey = "b81cfafcf029b12e24e9cb798b34031ec9335bd19ef7be14f383cb87edb1bed9";
        
        MosAccount account = new MosAccount(1L);
        account.setAccountId("28080");
        account.setApiKey(apiKey.getBytes());
        MosAPI api = new MosAPI();
        
        Map<String,String> fields = new HashMap<String,String>();
        //fields.put("timeStamp", "%3E," + ISO8061DateTimeConverter.dateToString(new SimpleDateFormat("yyyy-MM-dd").parse("2013-12-10")));
        fields.put("customerID", "40684");
        
        MosRequest request = new MosRequest(api, account, "Customer", fields);
        for(ExternalDTO item : request){
            System.out.println(item);
            break;
        }
    }
}
