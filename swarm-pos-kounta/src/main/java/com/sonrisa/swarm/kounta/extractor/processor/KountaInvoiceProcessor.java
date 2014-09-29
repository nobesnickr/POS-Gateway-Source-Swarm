package com.sonrisa.swarm.kounta.extractor.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.kounta.KountaAccount;
import com.sonrisa.swarm.kounta.KountaUriBuilder;
import com.sonrisa.swarm.kounta.dto.KountaCustomerDTO;
import com.sonrisa.swarm.kounta.dto.KountaInvoiceDTO;
import com.sonrisa.swarm.kounta.dto.KountaInvoiceLineDTO;
import com.sonrisa.swarm.kounta.dto.KountaProductDTO;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseExternalProcessor;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;

/**
 * Processor for {@link KountaInvoiceDTO} which fetches detailed pages by remote
 * id and inserts new {@link InvoiceDTO}, {@link ProductDTO} and {@link CustomerDTO} entities
 * based on that page. 
 * 
 * @author Barnabas
 *
 */
@Component("kountaInvoiceProcessor")
public class KountaInvoiceProcessor extends BaseExternalProcessor<KountaAccount, InvoiceEntity>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KountaInvoiceProcessor.class);
    
    /**
     * API reader to access remote content
     */
    private ExternalAPIReader<KountaAccount> apiReader;
    
    
    /**
     * Initialize
     * @param apiReader API reader to read single {@link ExternalDTO} entities
     */
    @Autowired
    public KountaInvoiceProcessor(@Qualifier("kountaItemAPIReader") ExternalAPIReader<KountaAccount> apiReader) {
        this.apiReader = apiReader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processEntity(KountaAccount account, InvoiceEntity item, SwarmDataWarehouse dataWarehouse) throws ExternalExtractorException {
        
        final String restUri = orderIdToUri(account, item.getLsInvoiceId());
        LOGGER.debug("Processing Kounta invoice ({}): {}", restUri, item);
        
        // Read remote content
        ExternalDTO invoiceNode = apiReader.getPage(
                new ExternalCommand<KountaAccount>(account, restUri), 0).getContent();
        
        // Map to invoice
        KountaInvoiceDTO detailedInvoice = new KountaInvoiceDTO();
        
        // Certain fields may only be available in the InvoiceDTO item
        detailedInvoice.setTotal(item.getTotal().doubleValue());
        detailedInvoice.setId(item.getLsInvoiceId());
        
        // Copy all other fields from invoiceNo
        getDtoTransformer().transformDTO(detailedInvoice, invoiceNode, KountaInvoiceDTO.class);
        
        // Save customer
        KountaCustomerDTO customer = getNestedItemAsDTO(invoiceNode, "customer", KountaCustomerDTO.class);
        if(customer != null){
        
            customer.setLastModified(detailedInvoice.getLastModified());
            dataWarehouse.save(account, Arrays.asList(customer), CustomerDTO.class);
            
            detailedInvoice.setCustomerId(customer.getRemoteId());
        }
        
        // JSON key for invoices lines
        final String keyForLines = "lines";
        
        // Map lines and products
        if(invoiceNode.hasKey(keyForLines)){
            
            List<InvoiceLineDTO> invoiceLines = new ArrayList<InvoiceLineDTO>();
            List<ProductDTO> products = new ArrayList<ProductDTO>();
            
            for(ExternalDTO lineNode : invoiceNode.getNestedItems(keyForLines)){
                KountaInvoiceLineDTO invoiceLine = getDtoTransformer().transformDTO(lineNode, KountaInvoiceLineDTO.class);
                invoiceLine.setInvoiceId(detailedInvoice.getRemoteId());
                invoiceLine.setLastModified(detailedInvoice.getLastModified());
                
                KountaProductDTO product = getNestedItemAsDTO(lineNode, "product", KountaProductDTO.class);
                if(product != null){
                    product.setUnitPrice(invoiceLine.getPrice());
                    product.setLastModified(detailedInvoice.getLastModified());
                    invoiceLine.setProductId(product.getRemoteId());
                    products.add(product);
                }
                
                invoiceLines.add(invoiceLine);
            }

            dataWarehouse.save(account, products, ProductDTO.class);
            dataWarehouse.save(account, invoiceLines, InvoiceLineDTO.class);
        }
        
        detailedInvoice.setDetailed(true);
        dataWarehouse.save(account, Arrays.asList(detailedInvoice), InvoiceDTO.class);
    }
    
    /**
     * Read nested item inside an {@link ExternalDTO} as a {@link DWTransferable}
     */
    private <T extends DWTransferable> T getNestedItemAsDTO(ExternalDTO source, String jsonKey, Class<T> clazz) throws ExternalExtractorException{
        if(source.hasKey(jsonKey)){
            return getDtoTransformer().transformDTO(source.getNestedItem(new ExternalDTOPath(jsonKey)), clazz);
        } else {
            return null;
        }
    }
    
    /**
     * Get the REST resource URI for a given order id
     */
    private static final String orderIdToUri(KountaAccount account, Long orderId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(KountaUriBuilder.getCompanyUri(account, "orders"));
        return stringBuilder.append("/").append(orderId).append(".json").toString();
    }
}
