package com.sonrisa.swarm.aspect.logging;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sonrisa.swarm.message.MessageService;
import com.sonrisa.swarm.posintegration.extractor.SwarmStore;

@Aspect
public class JobLoggingAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobLoggingAspect.class);

	private static final String POS_ERROR_MSG = "The system has detected a failure extracting information from ";
	private static final String ACCOUNT_MSG = "Affected account id: ";
	private static final String STORE_MSG = "Affected store id: ";
	private static final String EXCEPTION_MSG = "\nException stack trace: \n";
	
    @Value("${email.to}")
    private String to = "";
	
	@Autowired
	private MessageService messageService;
	
	@AfterThrowing(
			pointcut = "execution(* com.sonrisa.swarm.*.extractor.*.fetchData(..))", 
			throwing = "error")
    public void sendErrors(JoinPoint point, Throwable error){
		try{
			String packageName = point.getTarget().getClass().toString();
			String posName = getPOSNameFromPackage(packageName).toUpperCase();
			String msg = POS_ERROR_MSG + posName + "\n";
	
			Object[] arguments = point.getArgs();
			if (arguments != null && arguments.length > 0 && (arguments[0] instanceof SwarmStore)){
				SwarmStore store = ((SwarmStore) arguments[0]);
				msg += ACCOUNT_MSG + store.getAccountId() + "\n";
				msg += STORE_MSG + store.getStoreId() + "\n";
			}
			
			msg += EXCEPTION_MSG+ExceptionUtils.getStackTrace(error);
	
			messageService.sendMessage(msg, to, "Failure in "+posName+" extractor.");
		}catch(Exception e){
			LOGGER.error("Exception: ",e);
		}
    }
	
	
	/*
	@Around("execution(* com.sonrisa.swarm.*.job.*.process(..))")
	public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {      

	   	long start = System.currentTimeMillis();
		Object output = pjp.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
	   
		Object[] arguments = pjp.getArgs();
		if (arguments != null && arguments.length > 0 && (arguments[0] instanceof SwarmStore)){
			SwarmStore store = ((SwarmStore) arguments[0]);
			LOGGER.info("Extraction, for store [" + store.getStoreId() + "] was executed in: " + elapsedTime + " (ms)");
		}
		return output;
	}
	*/
	
	/**
	 * 
	 */
	private static String getPOSNameFromPackage(String pkg){
		String posName = "NOT FOUND";
		String[] splitPkg = pkg.split("\\.");
		if (splitPkg.length > 3){
			posName = splitPkg[3]; 			
		}
		return posName;
	}
	
	public static void main(String[] args) {
		System.out.println(getPOSNameFromPackage("class com.sonrisa.swarm.kounta.extractor.KountaExtractor"));
	}
}
