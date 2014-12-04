package com.sonrisa.swarm.vend.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.test.service.store.BaseStoreServiceTest;
import com.sonrisa.swarm.vend.VendAPI;
import com.sonrisa.swarm.vend.VendAccount;

/**
 * Class testing the {@link VendStoreServiceImpl}
 */
public class VendStoreServiceImplTest extends BaseStoreServiceTest<VendAccount> {
    
    /**
     * Mocked API
     */
    @Mock
    private VendAPI mockApi;
    
    /**
     * Target being tested
     */
    private VendStoreServiceImpl target;
    
    /**
     * API name for Vend
     */
    private static final String API_NAME = "vend_pos";
    
    /**
     * API ID for Vend
     */
    private static final Long API_ID = 10L;
    
    /**
     * Temporary OAuth code
     */
    private final String CODE = "sonrisaSONRISAsonrisa";
    
    /**
     * Mock account containing a refresh token (as API with responsd from {@link VendAPI#getAccountForTemporaryToken(String)}
     */
    private VendAccount account;
    
    /**
     * Sets up target
     * @throws ExternalExtractorException 
     */
    @Before
    public void setupTarget() throws ExternalExtractorException{
    	//TODO Uncomment
    	
/*    	setupApiService(API_NAME, API_ID);

        account = new VendAccount(0L);
        account.setOauthRefreshToken("abc");
        
        // Exchanging token
        when(mockApi.getAccountForTemporaryToken(eq(CODE)))
            .thenReturn(account);
        
        // Fetching company
        when(mockApi.sendRequest(argThat(new ExternalCommandMatcher<VendAccount>(VendUriBuilder.COMPANY_INFO_URI).andAccount(account))))
            .thenReturn(MockDataUtil.getResourceAsExternalResponse(MockVendData.MOCK_VEND_COMPANY));
        
        // Fetching sites
        when(mockApi.sendRequest(
                argThat(new ExternalCommandMatcher<VendAccount>(VendUriBuilder.getCompanyUri("1151", "sites.json"))
                        .andAccount(account))))
            .thenReturn(new ExternalResponse(
                    MockDataUtil.getResourceAsExternalJson(MockVendData.MOCK_VEND_SITES),
                    new HashMap<String,String>(){{
                        put("X-Pages", "1");
                    }}));
        
        target = new VendStoreServiceImpl();
        target.setStoreService(mockStoreService);
        target.setApiService(mockApiService);
        target.setAesUtility(aesUtility);
        target.setApi(mockApi);
        target.setApiReader(new VendAPIReader(mockApi));
        target.setApiName(API_NAME);*/
    }

    /**
     * Test case:
     *  Saving Vend stores using temporary OAuth code
     *  
     * Expected:
     *  Token service is used to convert temporary token to
     *  permanent.
     *  
     *  <i>Company</i> REST service is used to access to access
     *  company id and store name.
     */
    @Test
    @Ignore //TODO
    public void testCommonStoreFields() throws Exception {
        
        // Act
        VendAccount result = target.createAccountFromTemporaryToken(CODE);
        
        // Assert
        assertEquals("1151", result.getCompany());
        assertEquals("Swarm-Mobile", result.getStoreName());        
        assertNotNull(result.getOauthRefreshToken());
    }
    
    /**
     * Test case:
     *  Given a company and an access token, we can start create sites for the company
     *  
     * Expected:
     *  * Sites are fetched from <code>companies/.../sites.json
     *  * Separate {@link StoreEntity} is created for the new one
     */
    @Test
    @Ignore //TODO
    public void testCreatingStoreEntities() throws StoreScanningException{
        
        final String companyId = "1151";
        final String newLocation = "1410";
        
        account.setCompany(companyId);
        account.setStoreName("Sonrisa");
        
        // Act
        List<StoreEntity> result = target.scanForLocations(account);
        
        assertEquals(2, result.size());
        
        assertNull("New entity should be created", result.get(0).getId());
        assertEquals(API_ID, result.get(0).getApiId());
        assertEquals(account.getCompany(), aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(newLocation, result.get(0).getStoreFilter());
        assertEquals(account.getStoreName() + " - First", result.get(0).getName());
        assertEquals("New entities should be created as inactive", Boolean.FALSE, result.get(0).getActive());
        assertEquals(account.getOauthRefreshToken(), aesUtility.aesDecrypt(result.get(0).getOauthToken()));
    }
    

    /**
     * Test case:
     *  Given a company and an access token, we can start create sites for the company
     *  
     * Expected:
     *  * Sites are fetched from <code>companies/.../sites.json
     *  * Already existing entities are found and updated
     */
    @Test
    @Ignore //TODO
    public void testUpdatingStoreEntities () throws StoreScanningException{

        final String companyId = "1151";
        final String existingLocation = "1410";
        
        StoreEntity existingEntity = setupSingleActiveExistingStore(API_ID, companyId, null, existingLocation);

        account.setCompany(companyId);
        account.setStoreName("Sonrisa");
        
        // Act
        List<StoreEntity> result = target.scanForLocations(account);
        
        assertEquals(2, result.size());
        
        assertEquals(existingEntity.getId(), result.get(0).getId());
        assertEquals(API_ID, result.get(0).getApiId());
        assertEquals(account.getCompany(), aesUtility.aesDecrypt(result.get(0).getUsername()));
        assertEquals(existingLocation, result.get(0).getStoreFilter());
        assertEquals(existingEntity.getName(), result.get(0).getName());
        assertEquals(Boolean.TRUE, result.get(0).getActive());
        assertEquals(account.getOauthRefreshToken(), aesUtility.aesDecrypt(result.get(1).getOauthToken()));
    }
}
