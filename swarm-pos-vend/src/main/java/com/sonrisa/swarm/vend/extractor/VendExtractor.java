package com.sonrisa.swarm.vend.extractor;

import java.util.ArrayList;
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
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
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
    
	private static final String INVOICE_LINE_PATH = "register_sale_products";
	
	private static final int VEND_INITIAL_PAGE = 1;
	
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
    	
    	//Conversion to numeric Ids for Invoices and invoices_lines are 
		if(item instanceof VendCustomerDTO){
    		VendCustomerDTO customerDTO = ((VendCustomerDTO) item);
    		
    		customerDTO.setCustomerId(getNumericId(store, "customer", customerDTO.getUuidCustomerId()));
    	}else if(item instanceof VendProductDTO){
    		VendProductDTO productDTO = ((VendProductDTO) item);
    		
    		productDTO.setId(getNumericId(store, "product", productDTO.getUuidProductId()));
    	}else if(item instanceof VendOutletDTO){
    		VendOutletDTO outletDTO = ((VendOutletDTO) item);
    		
    		outletDTO.setRemoteId(getNumericId(store, "outlet", outletDTO.getUuidOutletId()));
    	}else if(item instanceof VendRegisterDTO){
    		VendRegisterDTO registerDTO = ((VendRegisterDTO) item);
    		
    		registerDTO.setRemoteId(getNumericId(store, "register", registerDTO.getUuidregisterId()));
    		registerDTO.setOutletId(getNumericId(store,"outlet", registerDTO.getUuidOutletId()));
    	}
    	
    	return item;
    }

    /**
     * This function translate a String UUID into a numeric ID using table Vend Ids table
     */
	private <T extends SwarmStore> long getNumericId(T store, String elemenType ,String uuid) {
		long id = -1;

		if (vendIdsDAO == null){
    		LOGGER.warn("VendsIdsDAO has not been initialized.");
    	}else{
    		id = vendIdsDAO.storeId(store.getStoreId(), elemenType, uuid);
    	}
		return id;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, VendAccount account, DWFilter since) {    	
    	Map<String, String> fields = new HashMap<String,String>();
        String restUrl;
        
        if(clazz == ProductDTO.class){
            restUrl = "products";
        } else if(clazz == CustomerDTO.class){
            restUrl = "customers";
        } else if(clazz == InvoiceDTO.class || clazz == InvoiceLineDTO.class){
            restUrl = "register_sales";
        }else if(clazz == OutletDTO.class){
        	restUrl = "outlets";
        }else if(clazz == RegisterDTO.class){
        	restUrl = "registers";
        } else {
            throw new IllegalArgumentException("Class should be one of the pos-integration abstract DTO classes");
        }
        
        // Setting the date filter and the Max number of elements returned by the Rest API
        fields.put(VendAPIReader.DATE_KEY, ISO8061DateTimeConverter.dateToString(since.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
        
        ExternalCommand<VendAccount> command = 
        		new ExternalCommand<VendAccount>(account, restUrl, fields);
        
        SimpleApiRequest<VendAccount> request = 
        		new SimpleApiRequest<VendAccount>(apiReader, command);
        
        if (request != null){
        	request.setFirstPage(VEND_INITIAL_PAGE);
        }
        
        return request;
    }
       
    protected void fetchInvoices(VendAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        
        Iterable<ExternalDTO> data = remoteRequest(InvoiceDTO.class, store, dataStore.getFilter(store, InvoiceDTO.class));

        ArrayList<VendInvoiceDTO> invoiceList = new ArrayList<VendInvoiceDTO>();
        ArrayList<VendInvoiceLineDTO> invoiceLineList = new ArrayList<VendInvoiceLineDTO>();

        // Iterate through all invoices
        for (ExternalDTO element : data) {
            VendInvoiceDTO invoice = getDtoTransformer().transformDTO(element, VendInvoiceDTO.class);
            setInvoiceNumericIDs(store, invoice);
            invoiceList.add(invoice);

            // invoice has array of rows for invoice lines
            for (ExternalDTO row : element.getNestedItems(INVOICE_LINE_PATH)) {
                    VendInvoiceLineDTO lineItem = getDtoTransformer().transformDTO(row, VendInvoiceLineDTO.class);
                    lineItem.setInvoiceId(invoice.getRemoteId());
                    lineItem.setTimestamp(invoice.getLastModified());
                    setInvoiceLineNumericIDs(store, lineItem);
                    invoiceLineList.add(lineItem);
            }

            // if limit exceeded save them to staging tables
            if (invoiceList.size() > QUEUE_LIMIT) {
                dataStore.save(store, invoiceList, InvoiceDTO.class);
                dataStore.save(store, invoiceLineList, InvoiceLineDTO.class);
                invoiceLineList.clear();
                invoiceList.clear();
            }
        }

        // save to the data warehouse (at this point list shouldn't be much
        // longer then the queueLimit
        if(!invoiceList.isEmpty()){
            dataStore.save(store, invoiceList, InvoiceDTO.class);
            dataStore.save(store, invoiceLineList, InvoiceLineDTO.class);
        }
    }
    

	private <T extends SwarmStore> void setInvoiceLineNumericIDs(T store, VendInvoiceLineDTO invoiceLineDTO) {
		invoiceLineDTO.setLineNumber(getNumericId(store, "sale_line", invoiceLineDTO.getStringLineNumber()));
		invoiceLineDTO.setProductId(getNumericId(store, "product", invoiceLineDTO.getUuidProductId()));
	}

	private <T extends SwarmStore> void setInvoiceNumericIDs(T store, VendInvoiceDTO invoiceDTO) {
		invoiceDTO.setId(getNumericId(store, "sale", invoiceDTO.getStringId()));
		invoiceDTO.setCustomerId(getNumericId(store, "customer", invoiceDTO.getStringCustomerId()));
		long registerId = getNumericId(store, "register", invoiceDTO.getUuidRegisterId());
		invoiceDTO.setRegisterId(registerId);
		
		if (vendIdsDAO != null){
			try{
				Long outletId =  vendIdsDAO.getForeignOutletIdForRegister(store.getStoreId(), registerId);
				invoiceDTO.setOutletId(outletId);
			}catch(EmptyResultDataAccessException e){
				LOGGER.debug("Register {} has not been stored yet. The object has been marked for future reprocessing.", registerId);
				invoiceDTO.setOutletId(-1L);
			}
		}
	}
	

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fetchInvoiceLines(VendAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
        return;
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
