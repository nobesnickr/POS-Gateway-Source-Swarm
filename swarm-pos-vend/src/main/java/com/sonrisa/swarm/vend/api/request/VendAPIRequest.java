package com.sonrisa.swarm.vend.api.request;

import java.util.Iterator;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

public class VendAPIRequest<T extends SwarmStore> extends SimpleApiRequest<T>{
	
	// Vend pages start in 1
	private static final int VEND_FIRST_PAGE = 1;

	public VendAPIRequest(ExternalAPIReader<T> dataReader,	ExternalCommand<T> command) {
		
		super(dataReader, command);
		setFirstPage(VEND_FIRST_PAGE);
	}

	@Override
    public Iterator<ExternalDTO> iterator() {
        return new VendExternalDTOExtractor<T>(dataReader, command, judge, firstPage);
    }
}
