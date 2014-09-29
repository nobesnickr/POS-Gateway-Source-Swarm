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

package com.sonrisa.swarm.posintegration.service.model;

import com.sonrisa.swarm.model.legacy.StoreEntity;

/**
 * Pseudo-Entity describing an API
 *  
 * @author Barnabas
 */
public class ApiEntity {

    /**
     * Value for the {@link StoreEntity#getApiId()}
     */
    private Long apiId = 0L;
    
    /**
     * Unique name per {@link ApiEntity}
     */
    private String apiName = "unknown";
    
    /**
     * Api's type
     */
    private ApiType apiType = ApiType.SWARM_API;
    
    /**
     * Kind of the API
     */
    public enum ApiType {
        PULL_API, // Shopify, Merchant OS, etc.
        RETAILPRO_API, // Retail Pro 8, Retail Pro 9
        SWARM_API // Non-gateway managed
    }
    
    public ApiEntity(Long apiId, String apiName) {
        this(apiId, apiName, ApiType.SWARM_API);
    }

    public ApiEntity(Long apiId, String apiName, ApiType apiType) {
        super();
        this.apiId = apiId;
        this.apiName = apiName;
        this.apiType = apiType;
    }

    public Long getApiId() {
        return apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public ApiType getApiType() {
        return apiType;
    }
    
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }

    @Override
    public String toString() {
        return apiName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apiId == null) ? 0 : apiId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApiEntity other = (ApiEntity) obj;
        if (apiId == null) {
            if (other.apiId != null)
                return false;
        } else if (!apiId.equals(other.apiId))
            return false;
        return true;
    }
}
