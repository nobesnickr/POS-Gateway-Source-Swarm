package com.sonrisa.swarm.vend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.admin.service.BaseStoreRegistrationService;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.api.service.exception.StoreScanningException;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.vend.VendAPI;
import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.vend.api.util.VendAPIReader;
import com.sonrisa.swarm.vend.service.VendStoreService;

/**
 * Service to register new {@link VendAccount} into the <code>stores</code> table.
 */
@Service
public class VendStoreServiceImpl extends BaseStoreRegistrationService implements VendStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendStoreServiceImpl.class);
        
    /**
     * Vend API to access remote content directly
     */
    @Autowired
    private VendAPI api;
    
    /**
     * Vend api reader
     */
    @Autowired
    private VendAPIReader apiReader;
    
    /**
     * Name of Vend api
     */
    @Value("${api.name.vend}")
    private String apiName;
    

    /**
     * {@inheritDoc}
     */
    @Override
    public VendAccount createAccountFromTemporaryToken(String code) throws StoreScanningException, ExternalExtractorException {
        VendAccount account = api.getAccountForTemporaryToken(code);
        
        LOGGER.debug("Resolving company for temporary code: {}", code);
        
        ExternalResponse response = api.sendRequest(new ExternalCommand<VendAccount>(account, ""));
        ExternalDTO companyInfo = response.getContent();
        
        final String companyId = companyInfo.getText("id"); 
        if(StringUtils.isEmpty(companyId)){
            throw new StoreScanningException("Illegal API response when reading company information");
        }
        account.setCompany(companyId);
        account.setStoreName(companyInfo.getText("name"));
        
        LOGGER.debug("Company resolved: {} ({})", companyInfo.getText("name"), companyId);
        
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StoreEntity> scanForLocations(VendAccount account) throws StoreScanningException {
        
        Iterable<ExternalDTO> sites = new SimpleApiRequest<VendAccount>(
                        apiReader, 
                        new ExternalCommand<VendAccount>(account, ""));

        List<StoreEntity> retVal = new ArrayList<StoreEntity>();
        try {
            for(ExternalDTO siteNode : sites){
                StoreEntity store = findOrCreateStore(
                        // stores.name
                        getStoreName(account.getStoreName(),siteNode.getText("name")),
                        // stores.username
                        account.getCompany(),
                        // stores.store_filter
                        siteNode.getText("id"),
                        // apis.name
                        apiName);
                
                store.setOauthToken(aesUtility.aesEncryptToBytes(account.getOauthRefreshToken()));
                store.setNotes(siteNode.getText("code"));
                retVal.add(store);
            }
        } catch (ExternalPageIterationException e){
            throw new StoreScanningException("Failed to read sites for company: " + account.getCompany(), e);
        }
        
        return retVal;
    }
    
    /**
     * Get store name from company & site name.
     */
    private static String getStoreName(String rootName, String siteName){
        StringBuilder builder = new StringBuilder();
        return builder.append(rootName).append(" - ").append(siteName).toString();
    }
    
    public void setApi(VendAPI api) {
        this.api = api;
    }

    public void setApiReader(VendAPIReader apiReader) {
        this.apiReader = apiReader;
    }

    public void setApiName(String vendApi) {
        this.apiName = vendApi;
    }
}
