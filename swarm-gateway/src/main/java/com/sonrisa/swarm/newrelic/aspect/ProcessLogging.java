package com.sonrisa.swarm.newrelic.aspect;

import java.util.Date;
import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

@Aspect
public class ProcessLogging extends AbstractLogging{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessLogging.class);
	//com.sonrisa.swarm.vend.job.VendExtractorLauncher.java
	
	@Around("execution(* com.sonrisa.swarm.job.ExtractorLauncher.process(..)) ||"
		  + "execution(* com.sonrisa.swarm.staging.job.loader.StagingEntityProcessor.process(..))")
	@Trace (dispatcher=true)
	public Object processLogforNR(ProceedingJoinPoint point) throws Throwable { 
		Object response = null;
		setTransaction(point);
		try{
			response = point.proceed();
		}catch(Exception e){
			notifyException(point, e);
		}
		return response;
	}

	private void setTransaction(ProceedingJoinPoint point) {
		try{
			String transactionType = (isExtractor(point))?
						"POS Extractor":
						"Staging Extractor";
			String stId = getStoreId(point);
			String posName = getPOSNameFromPackage(point);
			
			NewRelic.setTransactionName(transactionType, (null==stId)?posName:stId);
			NewRelic.addCustomParameter("POS", posName);
			NewRelic.addCustomParameter("Store Id", getStoreId(point));
		}catch(Exception e){
			LOGGER.error("Exception initializing new relic transaction: ", e);
		}
	}

	@Override
	protected void setParams(String posName, Object[] arguments, HashMap<String, String> params) {
		if (arguments[0] instanceof SwarmStore){
			StoreEntity store = (StoreEntity) arguments[0];
			params.put("POS", posName);
			params.put("Account id",Long.toString(store.getId()));
			params.put("Store name", store.getName());
		}
	}

	private String getStoreId(ProceedingJoinPoint point){
		Object[] arguments = point.getArgs();
		String storeId = null;
		if (arguments != null && arguments.length > 0){
			if	(arguments[0] instanceof StoreEntity){
				StoreEntity store = (StoreEntity) arguments[0];
				storeId = Long.toString(store.getId());
			}
			if (arguments[0] instanceof BaseStageEntity){
				BaseStageEntity stageEntity = (BaseStageEntity) arguments[0];
				Long storeIdl = stageEntity.getStoreId();
			
				storeId = (storeIdl == null)?null:Long.toString(storeIdl);
			}
		}
		
		return storeId;
	}
	
	private boolean isExtractor(ProceedingJoinPoint point){
		Object[] arguments = point.getArgs();
		return (arguments != null && arguments.length > 0 && 
				(arguments[0] instanceof StoreEntity));
	}
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}
	
	public static void main(String[] args) {
		System.out.println(new Date(1420878513717L));
	}
}
