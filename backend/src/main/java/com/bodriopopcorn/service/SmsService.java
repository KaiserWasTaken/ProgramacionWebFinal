package com.bodriopopcorn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.twilio.exception.ApiException;

@Service
public class SmsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

	private final TwilioMessagingClient twilioClient;
	private final boolean trialMode;

	public SmsService(TwilioMessagingClient twilioClient, @Value("${twilio.sms-trial-mode:false}") boolean trialMode) {
		this.twilioClient = twilioClient;
		this.trialMode = trialMode;
	}

	@Async
	public void sendPasswordChangedNotice(String phone) {
		if (phone == null || phone.isBlank()) {
			LOGGER.info("Password-change SMS skipped because the user has no phone number.");
			return;
		}

		try {
			String message = trialMode ? "sms_2fa" : "La contraseña de tu cuenta de BodrioPopcorn fue restablecida.";
			twilioClient.sendSms(phone, message);
		} catch (ApiException | IllegalArgumentException | IllegalStateException exception) {
			LOGGER.error("Password-change SMS could not be delivered.", exception);
		}
	}
}
