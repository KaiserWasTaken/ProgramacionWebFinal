package com.bodriopopcorn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class NotificationScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationScheduler.class);

	public void afterCommit(Runnable notification) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()
				|| !TransactionSynchronizationManager.isActualTransactionActive()) {
			run(notification);
			return;
		}

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				run(notification);
			}
		});
	}

	private void run(Runnable notification) {
		try {
			notification.run();
		} catch (RuntimeException exception) {
			LOGGER.error("Could not queue an asynchronous notification.", exception);
		}
	}
}
