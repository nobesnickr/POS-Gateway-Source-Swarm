package com.sonrisa.swarm.vend.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;
import com.sonrisa.swarm.posintegration.api.request.SimpleExternalDTOIterator;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.exception.ExternalPageIterationException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalJsonDTO;

public class VendExternalDTOExtractor<T extends SwarmStore> extends SimpleExternalDTOIterator<T>{
	private static final Logger LOGGER = LoggerFactory.getLogger(VendExternalDTOExtractor.class);
	
	/** Invoice Line Index (only for invoices lines) */
	private int invoiceLineIndex = 0;
	ExternalDTOPath invoiceLinePath = new ExternalDTOPath("register_sale_products");
	
	public VendExternalDTOExtractor(ExternalAPIReader<T> dataReader,
			ExternalCommand<T> command, ExternalIterationJudging judge,	int firstPage) {
		
		super(dataReader, command, judge, firstPage);
	}
	
	@Override
    public boolean hasNext() {
		boolean hasNext = super.hasNext();
		
		// Checking if it is an invoice without line
		if(isVendInvoiceLine(command) && hasNext){
			ExternalDTO invoice = null;
			try {
				invoice = getInvoice();       		
				getInvoiceLine(invoice, invoiceLinePath);
			} catch (ExternalExtractorException e) {
				
				try {
					invoice = getInvoice();
				} catch (ExternalExtractorException e1) {}
				LOGGER.warn("An invoice without lines has been detected. The invoive has been ignored.\n"+invoice,e);
				indexWithinPage++;
				invoiceLineIndex =0;
				hasNext = hasNext();
			}
		}
		return hasNext;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTO next() {
    	LOGGER.info("next()");
        // Fetching new page is done in hasNext when
        // reaching the end of each page
        if (hasNext()) {
            try {
            	ExternalJsonDTO extDTO = null;
            	
            	// If the command is looking for invoices lines we have to 
            	// extract the next element in a different way
            	if (isVendInvoiceLine(command)){
            		LOGGER.debug("The command is a invoice line for vend.");
            		
            		ExternalDTO invoiceDTO = getInvoice();
            		
            		LOGGER.debug("Invoice IL: "+ invoiceDTO);
            		
            		extDTO =  getInvoiceLine(invoiceDTO, invoiceLinePath);
            		
            		// Storing the invoice id and the timestamp as a 
            		// parameter inside the JSON invoiceLine DTO
            		String invoiceId = getInvoice().getText("id");
            		String ts = getInvoice().getText("sale_date");
            		           		
            		extDTO.addParameter("invoiceId", invoiceId);
            		extDTO.addParameter("sale_date", ts);

            		int numLines = invoiceDTO
            				.getNestedItemSize(invoiceLinePath);
        
            		
            		if (numLines-1 > invoiceLineIndex){
            			invoiceLineIndex++;
            		}else{
            			invoiceLineIndex = 0;
            			indexWithinPage++;
            		}
            	}else{
            		
                	extDTO =  (ExternalJsonDTO) currentPage.getContent()
                            .getNestedItem(dataReader.getDataKey(command))
                            .getNestedArrayItem(indexWithinPage++);
            	}
        		LOGGER.info("indexWithinPage: "+indexWithinPage);
            	return extDTO;
            } catch (ExternalExtractorException e) {
                throw new ExternalPageIterationException(e);
            }
        }
        return null;
    }

	private ExternalJsonDTO getInvoiceLine(ExternalDTO invoiceDTO,
			ExternalDTOPath invoiceLinePath) throws ExternalExtractorException {
		return (ExternalJsonDTO) invoiceDTO
				.getNestedItem(invoiceLinePath)
				.getNestedArrayItem(invoiceLineIndex);
	}

	private ExternalDTO getInvoice() throws ExternalExtractorException {
		return currentPage.getContent()
				.getNestedItem(dataReader.getDataKey(command))
		        .getNestedArrayItem(indexWithinPage);
	}
    
    private boolean isVendInvoiceLine(ExternalCommand<T> command){
    	return command != null && command.getFlags() != null && 
    			"true".equalsIgnoreCase(command.getFlags().get("line"));
    }
}
