package com.sonrisa.swarm.message.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

    @Value("${email.host}")
    private String host = "";

    @Value("${email.port}")
    private Integer port = -1;
    
    @Value("${email.username}")
    private String username = "";
    
    @Value("${email.password}")
    private String password = "";
    
    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        return properties;
    }

	@Override
	public String toString() {
		return "EmailConfiguration [host=" + host + ", port=" + port
				+ ", username=" + username + ", password=" + password + "]";
	}
}
