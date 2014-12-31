package com.sonrisa.swarm.job.vend;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.BaseExtractionIntegrationTest;
import com.sonrisa.swarm.job.ExtractorLauncherWriter;
import com.sonrisa.swarm.mock.MockDataUtil;
import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.mock.vend.MockVendData;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.service.ExtractorMonitoringService;
import com.sonrisa.swarm.vend.extractor.VendExtractor;


/**
 * Integration test for the {@link VendExtractor} class.
 */
public class VendExtractorFullTest extends BaseExtractionIntegrationTest{
	 @Autowired
	    @Qualifier("vendExtractorLauncherTest")
	    private JobLauncherTestUtils vendExtratorJobUtil;
	    
	    @Autowired
	    @Qualifier("loaderJobTestUtil")
	    private JobLauncherTestUtils loaderJobUtil;

	    @Value("${api.name.vend}")
	    private String vendApiName;

	    /**
	     * Extractor monitoring service
	     */
	    @Autowired
	    private ExtractorMonitoringService extractorMonitoringService;
	    
	    /**
	     * Store id of the active store
	     */
	    private Long storeId;
	    
	    /**
	     * Initial setup of the mock service
	     * @throws ExternalExtractorException 
	     */
	    @Before
	    public void setUp() throws ExternalExtractorException{
	    	
	    	stubFor(post(urlMatching(".*"))
	                .willReturn(aResponse().withBody(MockDataUtil.getResourceAsString(MockVendData.MOCK_VEND_ACCESS_TOKEN))));
	    	creategetStub(MockVendData.MOCK_VEND_ORDER_BATCH, 		"/register_sales.*");
	    	creategetStub(MockVendData.MOCK_VEND_PRODUCTS_BATCH, 	"/products.*");
	    	creategetStub(MockVendData.MOCK_VEND_CUSTOMER_BATCH,	"/customers.*");
	    	creategetStub(MockVendData.MOCK_VEND_OUTLETS_BATCH, 	"/outlets.*");
	    	creategetStub(MockVendData.MOCK_VEND_REGISTERS_BATCH, 	"/registers.*");
	        
	        storeId = saveMockStoreEntities(vendApiName);
	    }

		private void creategetStub(String mockVendProducts, String urlPattern) {
			String responseBody = MockDataUtil.getResourceAsString(mockVendProducts);
			stubFor(get(urlMatching(urlPattern)).willReturn(aResponse().withBody(responseBody)));
		}
		

		/**
	     * Test case:
	     * Extract some order and check the {@code total} value in db
	     * 
	     * Expected result:
	     * "total_price" used for {@code total}
	     */
	    @Test
		public void testUsedPrice() {
			launchJob(vendExtratorJobUtil);
			InvoiceStage invoice = invoiceStgService.findSingle(new SimpleFilter<InvoiceStage>(InvoiceStage.class, new FilterParameter("lsInvoiceId", "211")));
			assertEquals(Double.parseDouble(invoice.getTotal()), 219, 0.01d);
		}
	    
	    /**
	     * Test case: 
	     *  - starts a mock Vend server
	     *  - extracts data from them into the staging db
	     *  - asserts the number of the records inserted into the staging db
	     *  - launches the loader job which moves the records from the staging db to the legacy db
	     *  - asserts the number of the records inserted into the legacy db
	     */
	    @Test
	    public void testExecution() {       
	        final Date testStart = new Date(); 
	        
	        final JobExecution extractionResult = launchJob(vendExtratorJobUtil);
	        
	        // we expect that 1 store has been fetched because 1 active store exists
	        final int numOfExtractedStores = extractionResult.getExecutionContext()
	                                            .getInt(ExtractorLauncherWriter.NUM_OF_STORES_EXTRACTED);
	        
	        assertEquals(1, numOfExtractedStores);
	        
	        // assert staging counts
	        Map<String, Integer> correctCount = MockVendData.getCountOfMockJsonItems();
	        assertStagingCount(new MockPosDataDescriptor(correctCount));
	        
	        // launch the loader job
	        launchJob(loaderJobUtil);
	        launchJob(loaderJobUtil);

	        // assert the legacy db
	        assertNonDummyLegacyCount(new MockPosDataDescriptor(correctCount));
	        assertStagingIsEmpty();
	        
	        final Date monitoringValue = extractorMonitoringService.getLastSuccessfulExecution(storeId);
	        assertNotNull("Monitoring value for " + storeId + " is missing ", monitoringValue);
	        assertTrue("New monitoring value should've been added for store " + storeId, testStart.before(monitoringValue));
	    }
}
