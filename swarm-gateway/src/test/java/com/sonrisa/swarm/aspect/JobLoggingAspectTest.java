package com.sonrisa.swarm.aspect;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.sonrisa.swarm.aspect.logging.JobLoggingAspect;
import com.sonrisa.swarm.message.MessageService;
import com.sonrisa.swarm.vend.extractor.VendExtractor;

@RunWith(MockitoJUnitRunner.class)
public class JobLoggingAspectTest {

	@Mock
	MessageService messageService;
	
	@Mock
	JoinPoint joinPoint;

	@Mock
	VendExtractor extractor;
	
	@InjectMocks
	JobLoggingAspect jobLoggingApect = new JobLoggingAspect();
	
	/**
	 * Testing case:
	 * 	- Exception captured.
	 *  - The email is send.
	 * @throws Throwable 
	 */
	@Test
	public void testSendErrors() throws Throwable{
		ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
		when(joinPoint.getTarget()).thenReturn(extractor);
		when(joinPoint.proceed()).thenThrow(new UnknownHostException());
		
		jobLoggingApect.processErrors(joinPoint);
		
		Mockito.verify(messageService).sendMessage(anyString(), anyString(), eq("Failure in VEND extractor."));
	}
	
	/**
	 * Testing case:
	 * 	- Exception captured.
	 *  - New exception is thrown sending the email.
	 *  - New exception is ignored
	 * @throws Throwable 
	 */
	@Test
	public void testErrorSendEmails() throws Throwable{
		ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
		when(joinPoint.proceed()).thenThrow(new UnknownHostException());
		when(joinPoint.getTarget()).thenThrow(new NullPointerException("Exception sending emails"));

		jobLoggingApect.processErrors(joinPoint);
	}
}
