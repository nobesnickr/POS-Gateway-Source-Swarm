package com.sonrisa.swarm.vend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.job.ExtractorLauncher;
import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.vend.extractor.VendExtractor;
import com.sonrisa.swarm.vend.service.VendStoreFactory;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * This tasklet is responsible for launching the given {@link VendExtractor}.
 */
public class VendExtractorLauncher extends ExtractorLauncher {

    /** External extractor to launch.  */
    @Autowired
    @Qualifier("vendExtractor")
    private ExternalExtractor<VendAccount> extractor;
    
    /**
     * Service converting {@link StoreEntity} to {@link VendAccount}
     */
    @Autowired
    private VendStoreFactory storeFactory;
        
    /** Datastore to write the received data into. */
    @Autowired
    private SwarmDataWarehouse dataStore;
    
    @Override
    protected SwarmStore createAccount(final StoreEntity store) {
        VendAccount account = storeFactory.getAccount(store);
        return account;
    }

    // -----------------------------------------------------------------------
    // ~ Setters / getters
    // -----------------------------------------------------------------------       

    @Override
    public ExternalExtractor<VendAccount> getExtractor() {
        return extractor;
    }

    public void setDataStore(SwarmDataWarehouse dataStore) {
        this.dataStore = dataStore;
    }

    public void setExtractor(ExternalExtractor<VendAccount> extractor) {
        this.extractor = extractor;
    }            

    @Override
    public SwarmDataWarehouse getDataStore() {
        return dataStore;
    }
}
