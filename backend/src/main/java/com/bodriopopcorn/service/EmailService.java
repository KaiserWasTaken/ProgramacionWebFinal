package com.bodriopopcorn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	private final ObjectProvider<JavaMailSender> mailSenders;
	private final String fromAddress;

	public EmailService(
			ObjectProvider<JavaMailSender> mailSenders,
			@Value("${app.mail.from:}") String fromAddress
	) {
		this.mailSenders = mailSenders;
		this.fromAddress = fromAddress;
	}

	@Async
	public void sendWelcomeEmail(String email, String fullName) {
		send(email, "Bienvenido a BodrioPopcorn",
				"Hola " + fullName + ",\n\nTu cuenta de BodrioPopcorn ya está lista.");
	}

	@Async
	public void sendPasswordResetEmail(String email, String fullName, String resetLink) {
		send(email, "Restablece tu contraseña de BodrioPopcorn",
				"Hola " + fullName + ",\n\nUsa este enlace para cambiar tu contraseña. "
						+ "El enlace vence en 30 minutos y solo puede usarse una vez:\n\n" + resetLink);
	}

	private void send(String recipient, String subject, String body) {
		JavaMailSender mailSender = mailSenders.getIfAvailable();
		if (fromAddress.isBlank() || mailSender == null) {
			LOGGER.warn("Email notification skipped because MAIL_FROM or SMTP configuration is missing.");
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		message.setTo(recipient);
		message.setSubject(subject);
		message.setText(body);
		try {
			mailSender.send(message);
		} catch (MailException exception) {
			LOGGER.error("Email notification could not be delivered.", exception);
		}
	}
}
