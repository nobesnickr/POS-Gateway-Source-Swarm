package com.sonrisa.swarm.vend.controller.model;


/**
 * Row for the registration success page of Vend
 */
public class StoreInfoEntity {
    
    /**
     * Store's ID in the Swarm legacy <code>stores</code> table
     */
    private Long storeId;
    
    /**
     * Store's name
     */
    private String storeName;

    /**
     * Creates new instance 
     * @param storeId Store's Swarm ID displayed in the result table's first column 
     * @param storeName Store's internal name display in the result table's second column
     */
    public StoreInfoEntity(Long storeId, String storeName) {
        super();
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
