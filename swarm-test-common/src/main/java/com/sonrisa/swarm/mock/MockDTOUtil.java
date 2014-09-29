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

package com.sonrisa.swarm.mock;

import static org.mockito.Mockito.*;
import java.sql.Timestamp;

import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.impl.SimpleSwarmAccount;
import com.sonrisa.swarm.posintegration.extractor.security.AESUtility;

/**
 * Class providing mock items from the 
 * Model DTO objects
 * 
 */
public class MockDTOUtil {
    
    /** This is a utility class. */
    private MockDTOUtil(){
    }
    
    /** Generate a mock Customer */
    public static CustomerDTO mockCustomerDTO(final long customerId){
        final String firstName = "Bill";
        final String lastName = "Gates";
        final String email = "bill@microsoft.com";
        final String phone = "123987456";
        final String address = "72/III, East 34th str";
        final String address2 = "Los Angeles, CA";
        final String city = "Los Angeles";
        final String postalCode = "84442";
        final String state = "CA";
        final String country = "USA";
        final long lastModified = 1375863224191L;
        final String notes = "Not really Bill Gates";
        
        return new CustomerDTO() {
            public String getState() {return state;}
            public String getPostalCode() {return postalCode;}
            public String getPhoneNumber() {return phone;}
            public String getNotes() {return notes;}
            public String getName() {return firstName + " " + lastName;}
            public String getLastName() {return lastName;}
            public Timestamp getLastModified() {return new Timestamp(lastModified);}
            public String getFirstName() {return firstName;}
            public String getEmail() {return email;}
            public long getRemoteId() {return customerId;}
            public String getCountry() { return country; }
            public String getCity() { return city; }
            public String getAddress2() { return address2; }
            public String getAddress() { return address; }
        };
    }

    /**
     * Generate mock product, with mock data
     */
    public static ProductDTO mockProductDTO(){
        return new ProductDTO() {
            public String getUpc() { return "45678965"; }
            public String getStoreSku() { return "123456778";}
            public long getRemoteId() { return 234L; }
            public double getPrice() { return 199.50; }
            public String getManufacturerName() { return "Sonrisa Inc"; }
            public Long getManufacturerId() { return null; }
            public Timestamp getLastModified() { return new Timestamp(1375863224191L); }
            public String getEan() { return "ABC123"; }
            public String getDescription() { return "Something Nice."; }
            public String getCategoryName() { return "Inf"; }
            public Long getCategoryId() { return null; }
        };
    }
    
    /** Create a mock InvoiceDTO object */
    public static InvoiceDTO mockInvoiceDTO(final long invoiceId){
        return new InvoiceDTO() {
            public double getTotal() { return 450.99; }
            public Timestamp getLastModified() {return new Timestamp(1375863224191L);}
            public String getInvoiceNumber() { return "ERTYU9222"; }
            public long getRemoteId() { return invoiceId; }
            public Long getCustomerId() { return 3333L; }
			public Timestamp getInvoiceTimestamp() {return new Timestamp(1375863225196L);}
        };
    }
    
    /**
     * Creates a mock SwarmStore
     */
    public static SwarmStore mockStore(final long storeId){
        SwarmStore store = mock(SwarmStore.class);
        when(store.getStoreId()).thenReturn(storeId);
        return store;
    }
}
