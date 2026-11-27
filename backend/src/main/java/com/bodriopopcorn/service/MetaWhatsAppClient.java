package com.bodriopopcorn.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MetaWhatsAppClient {

	private final RestClient client;
	private final String phoneNumberId;
	private final String accessToken;

	@Autowired
	public MetaWhatsAppClient(
			RestClient.Builder builder,
			@Value("${whatsapp.meta.phone-number-id:${META_WHATSAPP_PHONE_NUMBER_ID:}}") String phoneNumberId,
			@Value("${whatsapp.meta.access-token:${META_WHATSAPP_ACCESS_TOKEN:}}") String accessToken
	) {
		this(builder.requestFactory(requestFactory()).build(), phoneNumberId, accessToken);
	}

	public MetaWhatsAppClient(RestClient client, String phoneNumberId, String accessToken) {
		this.client = client;
		this.phoneNumberId = phoneNumberId;
		this.accessToken = accessToken;
	}

	public void sendWhatsApp(String recipient, String body) {
		if (phoneNumberId.isBlank() || accessToken.isBlank()) {
			throw new IllegalStateException("Meta WhatsApp phone number ID and access token must be configured.");
		}
		if (!phoneNumberId.matches("[0-9]+")) {
			throw new IllegalArgumentException("Meta WhatsApp phone number ID must contain only digits.");
		}

		client.post()
				.uri("https://graph.facebook.com/v23.0/{phoneNumberId}/messages", phoneNumberId)
				.headers(headers -> headers.setBearerAuth(accessToken))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of(
						"messaging_product", "whatsapp",
						"recipient_type", "individual",
						"to", recipient,
						"type", "text",
						"text", Map.of("body", body)))
				.retrieve()
				.toBodilessEntity();
	}

	private static SimpleClientHttpRequestFactory requestFactory() {
		var factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Duration.ofSeconds(5));
		factory.setReadTimeout(Duration.ofSeconds(10));
		return factory;
	}
}
