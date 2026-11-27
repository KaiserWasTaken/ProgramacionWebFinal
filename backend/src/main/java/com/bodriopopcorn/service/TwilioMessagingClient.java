package com.bodriopopcorn.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class TwilioMessagingClient {

	private final String accountSid;
	private final String authToken;
	private final String smsSender;
	private final Map<String, String> trialSenders;

	public TwilioMessagingClient(
			@Value("${twilio.account-sid:}") String accountSid,
			@Value("${twilio.auth-token:}") String authToken,
			@Value("${twilio.phone-number:}") String smsSender,
			@Value("${twilio.sms-trial-mode:false}") boolean trialMode,
			@Value("${twilio.sms-trial-senders:${TWILIO_SMS_TRIAL_SENDERS:}}") String trialSenderPairs
	) {
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.smsSender = smsSender;
		this.trialSenders = trialMode ? parseTrialSenders(trialSenderPairs) : Map.of();
	}

	public void sendSms(String recipient, String body) {
		String sender = smsSender;
		if (!trialSenders.isEmpty()) {
			sender = trialSenders.get(recipient);
			if (sender == null) {
				throw new IllegalStateException("No Twilio trial sender configured for the recipient.");
			}
		}
		send(recipient, sender, body);
	}

	private Map<String, String> parseTrialSenders(String pairs) {
		if (pairs.isBlank()) return Map.of();
		Map<String, String> senders = new HashMap<>();
		for (String pair : pairs.split(",", -1)) {
			String[] numbers = pair.trim().split(":", -1);
			if (numbers.length != 2 || !numbers[0].matches("\\+[1-9][0-9]{1,14}")
					|| !numbers[1].matches("\\+[1-9][0-9]{1,14}")
					|| senders.putIfAbsent(numbers[0], numbers[1]) != null) {
				throw new IllegalArgumentException("Invalid or duplicate Twilio trial sender mapping.");
			}
		}
		return Map.copyOf(senders);
	}

	private void send(String recipient, String sender, String body) {
		if (accountSid.isBlank() || authToken.isBlank() || sender.isBlank()) {
			throw new IllegalStateException("Twilio credentials and sender numbers must be configured.");
		}

		Message.creator(new PhoneNumber(recipient), new PhoneNumber(sender), body)
				.create(new TwilioRestClient.Builder(accountSid, authToken).build());
	}
}
