package com.sonrisa.swarm.staging.job.exception;

public class RegisterNotFoundException extends Exception {
	
	private static final long serialVersionUID = -4252828346229437782L;
	
	public RegisterNotFoundException(Long storeId, Long registerId) {
		super("Register "+registerId+" for store "+storeId+ " not found.");
	}
}
