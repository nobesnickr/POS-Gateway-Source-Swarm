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

package com.sonrisa.swarm.rics.extractor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.dto.CustomerDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.dto.InvoiceLineDTO;
import com.sonrisa.swarm.posintegration.dto.ProductDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.impl.BaseIteratingExtractor;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWFilter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;
import com.sonrisa.swarm.posintegration.warehouse.SwarmDataWarehouse;
import com.sonrisa.swarm.rics.RicsAccount;
import com.sonrisa.swarm.rics.api.RicsApi;
import com.sonrisa.swarm.rics.constants.RicsUri;
import com.sonrisa.swarm.rics.dto.RicsCustomerDTO;
import com.sonrisa.swarm.rics.dto.RicsInvoiceDTO;
import com.sonrisa.swarm.rics.dto.RicsInvoiceLineDTO;
import com.sonrisa.swarm.rics.dto.RicsProductDTO;

/**
 * Extractor for RICS. This class is responsible for executing 
 * an extraction session during which the recent sales entities (Invoices, Products, etc.)
 * are fetched from the REST server and moved to the data warehouse.
 *
 */
@Component("ricsExtractor")
public class RicsExtractor extends BaseIteratingExtractor<RicsAccount> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RicsExtractor.class);

	/** API reader */
	private ExternalAPIReader<RicsAccount> apiReader;

	/**
	 * Initializes a new extractor object
	 */
	@Autowired
	public RicsExtractor(@Qualifier("ricsAPIReader") ExternalAPIReader<RicsAccount> apiReader) {
		super("com.sonrisa.swarm.rics.dto.Rics");
		this.apiReader = apiReader;
	}

	@Override
	protected void fetchInvoices(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
		DWFilter filter = dataStore.getFilter(store, InvoiceDTO.class);

		List<InvoiceDTO> invoices = new ArrayList<InvoiceDTO>();
		List<InvoiceLineDTO> invoiceLines = new ArrayList<InvoiceLineDTO>();
		List<ProductDTO> products = new ArrayList<ProductDTO>();

		Iterable<ExternalDTO> page = remoteRequest(InvoiceDTO.class, store, filter);

		// iterate over batches
		for (ExternalDTO batchNode : page) {
			Timestamp batchStart = batchNode.getTimeStampISO8061("BatchStartDate");
			// iterate over invoices
			for (ExternalDTO invoiceNode : batchNode.getNestedItems("SaleHeaders")) {
				RicsInvoiceDTO invoice = getDtoTransformer().transformDTO(invoiceNode, RicsInvoiceDTO.class);
				invoice.setBatchStart(batchStart);
				invoice.setCustomerId(extractCustomerId(invoiceNode));

				// iterate over invoice lines
				extractLinesAndProducts(invoiceNode.getNestedItems("SaleDetails"), invoice, invoiceLines, products);

				invoices.add(invoice);

				// If list reaches a limit, save items into the stage, and clear the list
				if (invoices.size() > QUEUE_LIMIT) {
					save(invoices, invoiceLines, products, dataStore, store);
					invoices.clear();
					invoiceLines.clear();
					products.clear();
				}
			}
		}

		save(invoices, invoiceLines, products, dataStore, store);
	}

	/**
	 * Extracts the lines and the lines' product under a specific invoice 
	 * @param lineNodes iterator for invoiceLine data
	 * @param invoice the invoice that the lines belong to
	 * @param invoiceLines a list to hold the lines
	 * @param products list to hold the products
	 * @throws ExternalExtractorException 
	 */
	private void extractLinesAndProducts(Iterable<ExternalDTO> lineNodes, RicsInvoiceDTO invoice, List<InvoiceLineDTO> invoiceLines, List<ProductDTO> products)
			throws ExternalExtractorException {
		BigDecimal total = new BigDecimal("0.0");

		for (ExternalDTO lineItem : lineNodes) {
			RicsInvoiceLineDTO invoiceLine = getDtoTransformer().transformDTO(lineItem, RicsInvoiceLineDTO.class);
			invoiceLine.setInvoiceId(invoice.getRemoteId());

			total = total.add(invoiceLine.getPriceAsBigDecimal());

			// extract product from invoice line
			try {
				ProductDTO product = extractProduct(lineItem);

				products.add(product);

				invoiceLine.setProductId(product.getRemoteId());
			} catch (ExternalExtractorException e) {
				LOGGER.debug("There is no product data for invoice line: {}", invoiceLine.getRemoteId(), e);
				// no problem: No product data for invoice (skip it)
			}

			invoiceLines.add(invoiceLine);
		}

		invoice.setTotal(total);
	}

	/**
	 * Extract the Customer's id who made the purchase
	 * @param invoice the invoice data to search in
	 * @return the customer's id or 0 if there is no valid customer
	 * @throws ExternalExtractorException
	 */
	private Long extractCustomerId(ExternalDTO invoice) throws ExternalExtractorException {
		if (invoice.hasKey("Customer")) {
			ExternalDTO customer = invoice.getNestedItem(new ExternalDTOPath("Customer"));
			Long customerId = customer.getLong("AccountNumber");
			if (customerId == 0L) {
				LOGGER.debug("Invalid customer id found: " + customer.getText("AccountNumber"));
			}
			return customerId;
		}
		return 0L;
	}

	/**
	 * Creates a ProductDTO can be found under an invoiceLine
	 * @param invoiceLine contains the data for ProductDTO instantiation
	 * @return the created RicsProductDTO
	 * @throws ExternalExtractorException if there is no nested node with the datakey or the conversion failed.
	 */
	private ProductDTO extractProduct(ExternalDTO invoiceLine) throws ExternalExtractorException {
		ExternalDTO productDto = invoiceLine.getNestedItem(new ExternalDTOPath("ProductItem"));
		return getDtoTransformer().transformDTO(productDto, RicsProductDTO.class);
	}

	/**
	 * saves every dto can be found in the lists. At the end of the procedure all list will be cleared.
	 * @param invoices list containing the extracted invoices
	 * @param invoiceLines list containing the extracted invoiceLines 
	 * @param products list containing the extracted products
	 * @param dataStore the datastore object the item saved into
	 * @param store the store the items extracted for
	 */
	private void save(List<InvoiceDTO> invoices, List<InvoiceLineDTO> invoiceLines, List<ProductDTO> products, SwarmDataWarehouse dataStore, RicsAccount store) {
		logger().debug("invoices " + invoices);
		logger().debug("invoicelines " + invoiceLines);
		logger().debug("products: " + products);

		dataStore.save(store, invoices, InvoiceDTO.class);
		dataStore.save(store, products, ProductDTO.class);
		dataStore.save(store, invoiceLines, InvoiceLineDTO.class);
	}

	@Override
	protected void fetchCategories(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
		// we do not gather categories from RICS
	}

	@Override
	protected void fetchProducts(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
		// products are fetched and processed during invoiceLine gathering
	}

	@Override
	protected void fetchInvoiceLines(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
		// invoice lines are fetched and processed under fetchInvoices
	}

	@Override
	protected void fetchCustomers(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {

		DWFilter filter = dataStore.getFilter(store, CustomerDTO.class);

		List<RicsCustomerDTO> customers = new ArrayList<RicsCustomerDTO>();

		// iterate over customers
		for (ExternalDTO customerNode : remoteRequest(CustomerDTO.class, store, filter)) {
			RicsCustomerDTO customer = getDtoTransformer().transformDTO(customerNode, RicsCustomerDTO.class);

			// iterate over invoices
			if (customerNode.hasKey("MailingAddress")) {
				ExternalDTO addressNode = customerNode.getNestedItem(new ExternalDTOPath("MailingAddress"));
				customer.setAddress(addressNode.getText("Address"));
				customer.setCity(addressNode.getText("City"));
				customer.setPostalCode(addressNode.getText("PostalCode"));
				customer.setState(addressNode.getText("State"));
			}

			customers.add(customer);

			// If list reaches a limit, save items into the stage, and clear the list
			if (customers.size() > QUEUE_LIMIT) {
				logger().debug("customers " + customers);
				dataStore.save(store, customers, CustomerDTO.class);
				customers.clear();
			}
		}

		if (customers.size() > 0) {
			logger().debug("customers " + customers);
			dataStore.save(store, customers, CustomerDTO.class);
		}
	}

	@Override
	protected void fetchManufacturers(RicsAccount store, SwarmDataWarehouse dataStore) throws ExternalExtractorException {
		// we do not gather manufacturers from RICS
	}

	@Override
	protected Iterable<ExternalDTO> remoteRequest(Class<? extends DWTransferable> clazz, RicsAccount account, DWFilter since) {
		String restUri = "";
		Map<String, String> jsonData = new HashMap<String, String>();
		jsonData.put("StoreCode", account.getStoreCode());

		if (clazz == InvoiceDTO.class) {
			restUri = RicsUri.INVOICES.uri;
			// required fields
			jsonData.put("BatchStartDate", ISO8061DateTimeConverter.dateToOdataString(new Date(since.getTimestamp().getTime())));
			jsonData.put("BatchEndDate", RicsApi.DATE_MAX);
		} else if (clazz == CustomerDTO.class) {
			restUri = RicsUri.CUSTOMERS.uri;
			jsonData.put("ModifiedStart", ISO8061DateTimeConverter.dateToOdataString(new Date(since.getTimestamp().getTime())));
			jsonData.put("ModifiedEnd", RicsApi.DATE_MAX);
		}
		return new SimpleApiRequest<RicsAccount>(apiReader, new ExternalCommand<RicsAccount>(account, restUri, jsonData));
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
}
