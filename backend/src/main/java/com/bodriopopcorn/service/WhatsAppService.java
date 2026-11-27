package com.bodriopopcorn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.bodriopopcorn.entity.ReviewStatus;

@Service
public class WhatsAppService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppService.class);

	private final MetaWhatsAppClient metaClient;

	public WhatsAppService(MetaWhatsAppClient metaClient) {
		this.metaClient = metaClient;
	}

	@Async
	public void sendReviewStatus(String phone, String titleName, ReviewStatus status) {
		if (phone == null || phone.isBlank()) {
			LOGGER.info("Review-status WhatsApp notification skipped because the user has no phone number.");
			return;
		}

		String decision = status == ReviewStatus.APPROVED ? "aprobada" : "rechazada";
		try {
			metaClient.sendWhatsApp(phone,
					"Tu reseña de \"" + titleName + "\" fue " + decision + ".");
		} catch (RestClientResponseException exception) {
			LOGGER.error("WhatsApp provider rejected the notification with HTTP {}.", exception.getStatusCode());
		} catch (RestClientException | IllegalArgumentException | IllegalStateException exception) {
			LOGGER.error("Review-status WhatsApp notification could not be sent: {}", exception.getMessage());
		}
	}
}
