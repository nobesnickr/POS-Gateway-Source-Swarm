package com.sonrisa.swarm.message.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.sonrisa.swarm.message.MessageService;

@Service("mailService")
public class EmailMessageService implements MessageService{

	@Autowired
    private MailSender mailSender;
	
	@Override
	public boolean sendMessage(String messageBody, String receiver, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setText(messageBody);
        message.setSubject(subject);
        mailSender.send(message);
        return true;
	}
	
}
