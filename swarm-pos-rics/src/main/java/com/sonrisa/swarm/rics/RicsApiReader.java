package com.sonrisa.swarm.rics;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sonrisa.swarm.posintegration.api.ExternalAPI;
import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalDataKeyResolver;
import com.sonrisa.swarm.posintegration.api.reader.impl.ExternalOffsetAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.impl.URIBasedDataKeyResolver;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.rics.constants.RicsUri;

@Component("ricsAPIReader")
public class RicsApiReader implements ExternalAPIReader<RicsAccount> {

	private ExternalOffsetAPIReader<RicsAccount> reader;
	
	/**
	 * default number of invoiceLines to be requested on a page
	 */
	private int pageSize = 100; //Must be between 0 and 100

	@Autowired
	public RicsApiReader(@Qualifier("ricsApi") ExternalAPI<RicsAccount> api) {
		Map<String, String> uriDataKeyMap = new HashMap<String, String>();
		uriDataKeyMap.put(RicsUri.INVOICES.uri, RicsUri.INVOICES.datakey);
		uriDataKeyMap.put(RicsUri.CUSTOMERS.uri, RicsUri.CUSTOMERS.datakey);
		
		ExternalDataKeyResolver<RicsAccount> dataKeyResolver = new URIBasedDataKeyResolver<RicsAccount>(uriDataKeyMap);

		reader = new ExternalOffsetAPIReader<RicsAccount>(api);
		reader.setFetchSize(pageSize);
		reader.setFetchSizeKey("Take");
		reader.setOffsetKey("Skip");
		reader.setDataKeyResolver(dataKeyResolver);
	}

	/**
	 * Sets the number of required invoiceLines requested on a page
	 * The size of the page may be lower at the end the data 
	 * @param pageSize the maximum number of invoices on a page 
	 */
	public void setPageSize(int pageSize) {
		reader.setFetchSize(pageSize);
	}

	@Override
	public ExternalResponse getPage(ExternalCommand<RicsAccount> command, int page) throws ExternalExtractorException {
		return reader.getPage(command, page);
	}

	@Override
	public ExternalDTOPath getDataKey(ExternalCommand<RicsAccount> command) {
		return reader.getDataKey(command);
	}

	@Override
	public int getFetchSize() {
		return reader.getFetchSize();
	}
}
