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
package com.sonrisa.swarm.test.extractor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.mock.MockPosDataDescriptor;
import com.sonrisa.swarm.posintegration.dto.CategoryDTO;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ManufacturerDTO;
import com.sonrisa.swarm.posintegration.dto.OutletDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.dto.RegisterDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalExtractor;
import com.sonrisa.swarm.posintegration.extractor.ExternalProcessor;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.test.matcher.ListSizeMatcher;

/**
 * Base class for testing {@link ExternalExtractor} or {@link ExternalProcessor} implementations
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseWarehouseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseExtractorTest.class);

    /**
     * Mocked DataStore
     */
    protected SwarmDataWarehouse dataStore;

    /**
     * Filter date of the dataStore
     */
    protected DWFilter filter;

    @Captor
    protected ArgumentCaptor<List<? extends CategoryDTO>> categoryCaptor;

    @Captor
    protected ArgumentCaptor<List<? extends ManufacturerDTO>> manufacturerCaptor;

    @Captor
    protected ArgumentCaptor<List<? extends CustomerDTO>> customerCaptor;

    @Captor
    protected ArgumentCaptor<List<? extends ProductDTO>> productCaptor;

    @Captor
    protected ArgumentCaptor<List<? extends InvoiceDTO>> invoiceCaptor;

    @Captor
    protected ArgumentCaptor<List<? extends InvoiceLineDTO>> invoiceLineCaptor;
    
    /**
     * Initial setup of the mock service
     * 
     * @throws ExternalExtractorException
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUpBaseClass() throws ExternalExtractorException {

        this.filter = DWFilter.fromTimestamp(new Timestamp((new Date()).getTime()));
        this.filter.setId(12345);

        dataStore = mock(SwarmDataWarehouse.class);
        when(dataStore.getFilter(any(SwarmStore.class), any(Class.class))).thenReturn(filter);
        
        // When ever the mock is invoked log it, to help error exploration
        doAnswer(new Answer<Object>() {
            @SuppressWarnings("rawtypes")
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List<?> entities = (List)args[1];
                Class<?> clazz = (Class)args[2];
                
                LOGGER.debug("Saving {} {} entities into datastore for {}", clazz.getSimpleName(), entities.size(), args[0]);
                return "Saved!";
            }
        }).when(dataStore).save(any(SwarmStore.class), any(List.class), any(Class.class));
    }

    /**
     * Asserts that a DTO was saved into the datastore a certain number of times
     * 
     * Cause of failure can be:
     *  - ListSizeMatcher doesn't match
     *  - Expected list size is over {@link BaseIteratingExtractor#QUEUE_LIMIT}
     *  - Invocation of the {@link SwarmDataWarehouse#save(SwarmStore, List, Class)} didn't occur exactly once
     * 
     * @param dataStore
     * @param clazz
     * @param expected
     */
    protected static <T extends DWTransferable> void assertDTOSaved(SwarmDataWarehouse dataStore, MockPosDataDescriptor descriptor, Class<T> clazz) {
        if(descriptor.getCountForDTOClass(clazz) > 0){
            verify(dataStore, times(1))
                .save(any(SwarmStore.class),
                    argThat(new ListSizeMatcher<T>(descriptor.getCountForDTOClass(clazz))), eq(clazz));
        }
    }

    /**
     * Returns a list of all the DTO entities saved to the dataStore
     * 
     * Warning: Use {@link BaseExtractorTest#assertDTOSaved(SwarmDataWarehouse, MockPosDataDescriptor, Class)} first
     * 
     * @param store Account tested
     * @param captor Captor used to capture, use base class's captors
     * @param clazz Type of DTO, e.g. <i>InvoiceDTO.class</i>
     * @return List of containing the entities saved
     */
    @SuppressWarnings("unchecked")
    protected <X extends DWTransferable> List<X> getDtoFromCaptor(SwarmStore store, ArgumentCaptor<List<? extends X>> captor,Class<X> clazz) {
        verify(dataStore, times(1)).save(eq(store), captor.capture(), eq(clazz));
        return (List<X>) captor.getAllValues().get(0);
    }

    /**
     * Assert that the number of extracted DTO items matched the number in the
     * mock JSON files
     * 
     * @param posDataDescriptor POS data descriptor describing the number of expected MOCK entities
     */
    protected void assertQuantityOfItemsExtracted(MockPosDataDescriptor posDataDescriptor) {
        assertDTOSaved(dataStore, posDataDescriptor, CategoryDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, ManufacturerDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, ProductDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, CustomerDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, InvoiceDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, InvoiceLineDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, OutletDTO.class);
        assertDTOSaved(dataStore, posDataDescriptor, RegisterDTO.class);
    }
}
