package com.sonrisa.swarm.vend.extractor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.OutletDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.dto.RegisterDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.vend.VendAccount;
import com.sonrisa.swarm.vend.api.request.VendAPIRequest;
import com.sonrisa.swarm.vend.api.util.VendAPIReader;
import com.sonrisa.swarm.vend.dao.VendIdsDAO;
import com.sonrisa.swarm.vend.dto.VendCustomerDTO;
import com.sonrisa.swarm.vend.dto.VendInvoiceDTO;
import com.sonrisa.swarm.vend.dto.VendInvoiceLineDTO;
import com.sonrisa.swarm.vend.dto.VendOutletDTO;
import com.sonrisa.swarm.vend.dto.VendProductDTO;
import com.sonrisa.swarm.vend.dto.VendRegisterDTO;

/**
 * Extractor for Vend. This class is responsible for executing the batch extraction for invoices
 * meaning that the invoice's id, total and timestamp is to be written into the database,
 * with <code>lines_processed</code> set to false.
 * 
 * This class is thread safe.
 */
@Component("vendExtractor")
public class VendExtractor extends BaseIteratingExtractor<VendAccount> {

	private static final Logger LOGGER = LoggerFactory.getLogger(VendExtractor.class);
    
	@Autowired
	@Qualifier("vendIdsDAO")
	VendIdsDAO vendIdsDAO = null;
	
    /** API reader */
    private ExternalAPIReader<VendAccount> apiReader;
    
    /** Initializes an instance of the VendExtractor class */
    @Autowired
    public VendExtractor(@Qualifier("vendAPIReader") ExternalAPIReader<VendAccount> apiReader){ 
        super("com.sonrisa.swarm.vend.dto.Vend");
        this.apiReader = apiReader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected <W extends DWTransferable, T extends SwarmStore> W preprocessItem(W item, T store){   	
    	if (vendIdsDAO == null){
    		LOGGER.warn("VendsIdsDAO has not been initialized. Skiping preprocessing for item: "+item);
    		return item;
    	}
    	
		if (item instanceof VendInvoiceDTO){
		VendInvoiceDTO invoiceDTO = ((VendInvoiceDTO) item);
		
		
    		// Storing the Invoice String Id and obtaining the numeric id
    		long id = vendIdsDAO.storeId(store.getStoreId(), "sale", invoiceDTO.getStringId());
    		invoiceDTO.setId(id);
    		
    		// Storing the Customer String Id and obtaining the numeric id 		
    		long customerId = vendIdsDAO.storeId(store.getStoreId(), "customer", invoiceDTO.getStringCustomerId());
    		invoiceDTO.setCustomerId(customerId);
    		
    		// Storing the Register String Id and obtaining the numeric Id
    		long registerId = vendIdsDAO.storeId(store.getStoreId(), "register", invoiceDTO.getUuidRegisterId());
    		invoiceDTO.setRegisterId(registerId);
    		
    		try{
	    		Long outletId =  vendIdsDAO.getForeignOutletIdForRegister(store.getStoreId(), registerId);
	    		invoiceDTO.setOutletId(outletId);
    		}catch(EmptyResultDataAccessException e){
    			LOGGER.warn("Register "+registerId+"has not been stored yet. The object has been marked for future reprocessing.");
    			invoiceDTO.setOutletId(-1L);
    		}
    		
    	}else if(item instanceof VendInvoiceLineDTO){
    		VendInvoiceLineDTO invoiceLineDTO = ((VendInvoiceLineDTO) item);
    		
    		// Storing the Invoice line String Id and obtaining the numeric id
    		long id = vendIdsDAO.storeId(store.getStoreId(), "sale_line", invoiceLineDTO.getStringLineNumber());
    		invoiceLineDTO.setLineNumber(id);
    		
    		// Storing the Invoice String Id and obtaining the numeric id
    		long invoiceId = vendIdsDAO.storeId(store.getStoreId(), "sale", invoiceLineDTO.getUuidInvoiceId());
    		invoiceLineDTO.setInvoiceId(invoiceId);
    		
    		// Storing the Invoice String Id and obtaining the numeric id
    		long productId = vendIdsDAO.storeId(store.getStoreId(), "product", invoiceLineDTO.getUuidProductId());
    		invoiceLineDTO.setProductId(productId);
    	}else if(item instanceof VendCustomerDTO){
    		VendCustomerDTO customerDTO = ((VendCustomerDTO) item);
    		
    		// Storing the customer String Id and obtaining the numeric id
    		long id = vendIdsDAO.storeId(store.getStoreId(), "customer", customerDTO.getUuidCustomerId());
    		customerDTO.setCustomerId(id);
    	}else if(item instanceof VendProductDTO){
    		VendProductDTO productDTO = ((VendProductDTO) item);
    		
    		// Storing the product String Id and obtaining the numeric id
    		long id = vendIdsDAO.storeId(store.getStoreId(), "product", productDTO.getUuidProductId());
    		productDTO.setId(id);
    	}else if(item instanceof VendOutletDTO){
    		VendOutletDTO outletDTO = ((VendOutletDTO) item);
    		
    		// Storing the product String Id and obtaining the numeric id
    		long id = vendIdsDAO.storeId(store.getStoreId(), "outlet", outletDTO.getUuidOutletId());
    		outletDTO.setRemoteId(id);
    	}else if(item instanceof VendRegisterDTO){
    		VendRegisterDTO registerDTO = ((VendRegisterDTO) item);
    		
    		long id = vendIdsDAO.storeId(store.getStoreId(), "register", registerDTO.getUuidregisterId());
    		registerDTO.setRemoteId(id);
    		
    		long outletid = vendIdsDAO.storeId(store.getStoreId(), "register", registerDTO.getUuidOutletId());
    		registerDTO.setOutletId(outletid);
    	}
    	
    	return item;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, VendAccount account, DWFilter since) {
        Map<String, String> fields = new HashMap<String,String>();
        
        String restUrl;
        HashMap<String, String> flags = null;
        if(clazz == ProductDTO.class){
            restUrl = "products";
        } else if(clazz == CustomerDTO.class){
            restUrl = "customers";
        } else if(clazz == InvoiceDTO.class){
            restUrl = "register_sales";
        } else if(clazz == InvoiceLineDTO.class){
            restUrl = "register_sales";
            flags = new HashMap<String, String>();
            flags.put("line", "true");
        }else if(clazz == OutletDTO.class){
        	restUrl = "outlets";
        }else if(clazz == RegisterDTO.class){
        	restUrl = "registers";
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        // Setting the date filter and the Max number of elements returned by the Rest API
        fields.put(VendAPIReader.DATE_KEY, ISO8061DateTimeConverter.dateToString(since.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
        fields.put(VendAPIReader.PAGE_SIZE_KEY, Integer.toString(apiReader.getFetchSize()));
        
        ExternalCommand<VendAccount> command = 
        		new ExternalCommand<VendAccount>(account, restUrl, fields, flags);
        
        LOGGER.info(command.toString());
        return new VendAPIRequest<VendAccount>(apiReader, command);
    }
       
   
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchManufacturers(VendAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No manufacturers for Vend pro
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchCategories(VendAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
        // No categories for Vend
        return;
    }
    
    /**
     * Fetches the POS outlets
     */
    @Override
    protected void fetchOutlets(VendAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
    	fetchRemoteData(account, dataStore, OutletDTO.class, getPosSpecificClass(OutletDTO.class));
    }
    
    /**
     * Fetches the POS outlets
     */
    @Override
    protected void fetchRegisters(VendAccount account, SwarmDataWarehouse dataStore) throws ExternalExtractorException{
    	fetchRemoteData(account, dataStore, RegisterDTO.class, getPosSpecificClass(RegisterDTO.class));
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
