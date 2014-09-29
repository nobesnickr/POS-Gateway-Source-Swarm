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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalApiPagingRequest;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

/**
 * Merchant OS extractor connects to Merchant OS webservice and extracts
 * data from it. It transforms this data, and than saves it into 
 * the given datastore.
 */
@Component("MosExtractor")
public class MosExtractor extends BaseIteratingExtractor<MosAccount> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MosExtractor.class);

    /** The Merchant OS API communication with the remote server */
    private MosAPI api;
    
    /** Initializes an instance of the MosExtractor class */
    @Autowired
    public MosExtractor(MosAPI api){ 
        super("com.sonrisa.swarm.mos.dto.Mos");
        this.api = api;
    }


    @Override
    protected ExternalApiPagingRequest<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, MosAccount account, DWFilter since) {
        Map<String, String> fields = new HashMap<String,String>();
        
        Timestamp time = since.getTimestamp();
        
        //TODO when refactoring using ExternalCommand, don't use the URIEncoded version
        fields.put("timeStamp", "%3E," + ISO8061DateTimeConverter.dateToMerchantOSURIEncodedString(new Date(time.getTime())));
        
        final String shopFilter = account.getShopId();
        if(clazz == InvoiceDTO.class || clazz == InvoiceLineDTO.class){
            if(shopFilter != null && !StringUtils.isEmpty(shopFilter)){
                fields.put("shopID", "IN,[" + shopFilter + "]");
            }
        }
 
        String restUrl;
        if(clazz == CategoryDTO.class){
            restUrl = "Category";
        } else if(clazz == ManufacturerDTO.class){
            restUrl = "Manufacturer";
        } else if(clazz == ProductDTO.class){
            restUrl = "Item";
        } else if(clazz == CustomerDTO.class){
            restUrl = "Customer";
        } else if(clazz == InvoiceDTO.class){
            restUrl = "Sale";
        } else if(clazz == InvoiceLineDTO.class){
            restUrl = "SaleLine";
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        return new MosRequest(api, account, restUrl, fields);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
