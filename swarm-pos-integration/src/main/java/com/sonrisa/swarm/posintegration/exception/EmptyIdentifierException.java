package com.sonrisa.swarm.posintegration.exception;

import java.lang.annotation.ElementType;

public class EmptyIdentifierException extends Exception{

	private static final long serialVersionUID = -8250443880788065761L;

	public EmptyIdentifierException(Long storeId, String elementType) {
		super("Error storing a "+elementType+" with an empty identifier for store: "+storeId);
	}
}
