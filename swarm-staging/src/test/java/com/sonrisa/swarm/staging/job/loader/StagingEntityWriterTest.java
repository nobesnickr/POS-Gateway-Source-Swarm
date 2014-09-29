package com.sonrisa.swarm.staging.job.loader;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.sonrisa.swarm.legacy.service.BaseLegacyService;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.model.StageAndLegacyHolder;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.staging.job.exception.NonDistinctBatchItemsException;
import com.sonrisa.swarm.staging.service.BaseStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;


/**
 * Test class for the {@link StagingEntityWriter} class, which is responsible
 * for writing processed staging entities into the legacy tables.
 */
@RunWith(MockitoJUnitRunner.class)
public class StagingEntityWriterTest {
	
	/**
	 * Target being tested
	 */
	private StagingEntityWriter target;
	
	/**
	 * Captor to capture which invoices are deleted in stage
	 */
	@Captor
	private ArgumentCaptor<Collection<Long>> deletionCaptor;
	
	/**
	 * Mock staging service 
	 */
	private BaseStagingService<InvoiceStage> invoiceStgService;
	
	/**
	 * Mock legacy service
	 */
	private BaseLegacyService<InvoiceStage, InvoiceEntity> invoiceService;
	
	/**
	 * Mock store set up
	 */
	private StoreEntity mockStore;
	
	/**
	 * Sets up target by adding its mock dependencies
	 */
	@Before
	public void setupTarget(){

		// Used to delete instances 
		invoiceStgService = mock(InvoiceStagingService.class);
		
		// Mock service
		invoiceService = mock(InvoiceService.class);
		
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				InvoiceEntity entity = (InvoiceEntity)invocation.getArguments()[0];
				
				// For convenient debugging the generated legacy id is equals to the invoice number
				// when inserting new entity
				if(entity.getId() == null){
					entity.setId(Long.parseLong(entity.getInvoiceNo()));
				}
				
				// its actually void
				return null;
			}
		}).when(invoiceService).saveEntityFromStaging(any(InvoiceEntity.class));
		
		mockStore = new StoreEntity();
		mockStore.setId(1234L);
	}
	
	/**
	 * Test case:
	 *  Staging processor has processed a 3 invoices, two of which has
	 *  matching store_id & legacy system id
	 *  
	 * Expected:
	 *  Writer halts when the second duplication arrives
	 * @throws Exception 
	 */
	@Test(expected=NonDistinctBatchItemsException.class)
	public void testInvoiceEntityWriter() throws Exception {
		
		// Mock input
		List<StageAndLegacyHolder<InvoiceStage, InvoiceEntity>> input = new ArrayList<StageAndLegacyHolder<InvoiceStage, InvoiceEntity>>();
		
		// Insert all with null as legacy id (not found in legacy tables), 
		// and with different invoice, to test updating
		final Long[] stageIds = {100L, 101L, 102L};
		final Long[] invoiceNumbers = {1000L, 1001L, 1002L};
		
		input.add(mockInvoiceHolder(stageIds[0], 1L, null, invoiceNumbers[0], mockStore));
		input.add(mockInvoiceHolder(stageIds[1], 2L, null, invoiceNumbers[1], mockStore));
		input.add(mockInvoiceHolder(stageIds[2], 2L, null, invoiceNumbers[2], mockStore));
		
		target = new StagingEntityWriter();
		target.setLegacyService(invoiceService);
		target.setStagingService(invoiceStgService);
		
		// Act
		target.write(input);
	}
	
	/**
	 * Test case:
	 *  Staging processor has processed a 3 invoices, all of them where
	 *  found in the legacy tables and the last two has the same legacy id and 
	 *  store
	 *  
	 * Expected:
	 *  Updates all the original entries, if received multiple times, then
	 *  updates the legacy entities multiple times 
	 *  
	 * @throws Exception 
	 */
	@Test
	public void testInvoiceWriterUpdatingInvoices() throws Exception{
		
		// Mock input
		List<StageAndLegacyHolder<InvoiceStage, InvoiceEntity>> input = new ArrayList<StageAndLegacyHolder<InvoiceStage, InvoiceEntity>>();
		
		// Insert all with null as legacy id (not found in legacy tables), 
		// and with different invoice, to test updating
		final Long[] stageIds = {100L, 101L, 102L};
		final Long[] invoiceNumbers = {1000L, 1001L, 1002L};
		final Long[] legacyIds = {111L, 222L};
		
		// Setting the legacy id as if the processor has found them in the legacy tables
		input.add(mockInvoiceHolder(stageIds[0], 1L, legacyIds[0], invoiceNumbers[0], mockStore));
		input.add(mockInvoiceHolder(stageIds[1], 2L, legacyIds[1], invoiceNumbers[1], mockStore));
		input.add(mockInvoiceHolder(stageIds[2], 2L, legacyIds[1], invoiceNumbers[2], mockStore));
		
		target = new StagingEntityWriter();
		target.setLegacyService(invoiceService);
		target.setStagingService(invoiceStgService);
		
		// Act
		target.write(input);
		
		// Assert
		verify(invoiceStgService).delete(deletionCaptor.capture());
		assertTrue("Not all staging entities were deleted", deletionCaptor.getValue().containsAll(Arrays.asList(stageIds)));
		
		ArgumentCaptor<InvoiceEntity> saveCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
		verify(invoiceService,times(3)).saveEntityFromStaging(saveCaptor.capture());
		
		assertEquals("Should be inserted", legacyIds[0], saveCaptor.getAllValues().get(0).getId());
		assertEquals("Should be inserted", legacyIds[1], saveCaptor.getAllValues().get(1).getId());
		assertEquals("Should update previous invoice", legacyIds[1], saveCaptor.getAllValues().get(2).getId());
	}
		
	/**
	 * Create mock {@link StageAndLegacyHolder} with only id values filled in entities 
	 * 
	 * @param stageId StageEntity's id
	 * @param lsInvoiceId Legacy id set in stage and legacy entities
	 * @param legacyId Legacy id set for the legacy entity, or <i>null</i> simulating that its not yet in legacy tables
	 * @param invoiceNo Invoice number set for legacy and staging entities
	 * @param store Store entity set for the legacy entity
	 * @return 
	 */
	private static StageAndLegacyHolder<InvoiceStage, InvoiceEntity> mockInvoiceHolder(Long stageId, Long lsInvoiceId, Long legacyId, Long invoiceNo, StoreEntity store){
		
		InvoiceStage stageEntity = new InvoiceStage();
		stageEntity.setId(stageId);
		stageEntity.setLsInvoiceId(lsInvoiceId.toString());
		stageEntity.setStoreId(store.getId());
		stageEntity.setInvoiceNo(invoiceNo.toString());
		
		InvoiceEntity legacyEntity = new InvoiceEntity();
		legacyEntity.setId(legacyId);
		legacyEntity.setLsInvoiceId(lsInvoiceId);
		legacyEntity.setStore(store);
		legacyEntity.setInvoiceNo(invoiceNo.toString());
		
		return new StageAndLegacyHolder<InvoiceStage, InvoiceEntity>(legacyEntity, stageEntity);
	}
}
