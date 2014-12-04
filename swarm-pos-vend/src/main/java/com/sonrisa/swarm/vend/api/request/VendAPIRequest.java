package com.sonrisa.swarm.vend.api.request;

import java.util.Iterator;

import com.sonrisa.swarm.posintegration.api.ExternalCommand;
import com.sonrisa.swarm.posintegration.api.ExternalResponse;
import com.sonrisa.swarm.posintegration.api.reader.ExternalAPIReader;
import com.sonrisa.swarm.posintegration.api.reader.ExternalIterationJudging;
import com.sonrisa.swarm.posintegration.api.request.SimpleApiRequest;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

public class VendAPIRequest<T extends SwarmStore> extends SimpleApiRequest<T>{
	
	// Vend pages start in 1
	private static final int VEND_FIRST_PAGE = 1;

	public VendAPIRequest(ExternalAPIReader<T> dataReader,	ExternalCommand<T> command) {
		
		super(dataReader, command, new VendTerminateJudge(dataReader.getDataKey(command)));
		setFirstPage(VEND_FIRST_PAGE);
	}

	@Override
    public Iterator<ExternalDTO> iterator() {
        return new VendExternalDTOExtractor<T>(dataReader, command, judge, firstPage);
    }
	
    /**
     * Vend termination judge judges {@link ExternalDTO} by the 
     * number of data objects, and if its less then the fetch size it forces halting.
     */
    static private class VendTerminateJudge implements ExternalIterationJudging {
    	
    	private ExternalDTOPath path;
        
        public VendTerminateJudge(ExternalDTOPath path) {
            this.path = path;
        }

        @Override
        public boolean terminated(ExternalResponse lastNode, int pageNumber, int fetchSize) {
            return lastNode.getContent().getNestedItemSize(path) <= fetchSize;
        }
    }	
}
