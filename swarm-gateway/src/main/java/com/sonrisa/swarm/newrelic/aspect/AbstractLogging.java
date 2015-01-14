package com.sonrisa.swarm.newrelic.aspect;

import java.util.HashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import com.newrelic.api.agent.NewRelic;

@Aspect
public abstract class AbstractLogging {
	/**
	 * Obtain the POS name from the package
	 */
	protected String getPOSNameFromPackage(JoinPoint point){
		String packageName = point.getTarget().getClass().toString();
		String posName = "NOT FOUND";
		String[] splitPkg = packageName.split("\\.");
		if (splitPkg.length > 3){
			posName = splitPkg[3]; 			
		}
		
		if ("SHOPIFYEXTRACTOR".equalsIgnoreCase(posName)){
			posName = "SHOPIFY";
		}
		return posName;
	}
	
	protected void notifyException(ProceedingJoinPoint point, Exception e) {
		getLogger().error("Exception fetching data: ", e);
		try{
			String posName = getPOSNameFromPackage(point).toUpperCase();
			Object[] arguments = point.getArgs();
			HashMap<String, String> params = new HashMap<String, String>();
			if (arguments != null && arguments.length > 0){ 
				setParams(posName, arguments, params);
			}
			
			NewRelic.noticeError(e,params);
		}catch(Exception notificationException){ 
			getLogger().error("Exception notifying exception to new relic transaction: ", e);
		}
	}

	protected abstract void setParams(String posName, Object[] arguments, HashMap<String, String> params);
	protected abstract Logger getLogger();
}
