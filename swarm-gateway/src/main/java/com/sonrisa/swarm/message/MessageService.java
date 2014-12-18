package com.sonrisa.swarm.message;

public interface MessageService {
	boolean sendMessage(String message, String receiver, String subject);
}
