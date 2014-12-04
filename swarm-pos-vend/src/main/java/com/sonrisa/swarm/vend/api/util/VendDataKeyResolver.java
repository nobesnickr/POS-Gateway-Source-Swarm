package com.sonrisa.swarm.vend.api.util;

import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.posintegration.api.reader.impl.SimpleDataKeyResolver;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;

/**
 * Data key resolver for Vend.
 * 
 * As there is no data key for Vend this class returns a path to the data
 * to be the top level.
 */
public class VendDataKeyResolver extends SimpleDataKeyResolver<VendAccount>{

    /**
     * Initialize for root level data key
     */
    public VendDataKeyResolver() {
        super(ExternalDTOPath.getRootPath());
    }
}
