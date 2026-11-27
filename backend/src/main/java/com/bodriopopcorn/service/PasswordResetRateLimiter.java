package com.bodriopopcorn.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetRateLimiter {

	private static final int MAX_ATTEMPTS = 5;
	private static final long WINDOW_MILLIS = 15 * 60 * 1000;
	private static final int MAX_TRACKED_ADDRESSES = 10_000;

	private final Map<String, RequestWindow> requestsByAddress = new LinkedHashMap<>(16, 0.75f, true);

	public synchronized long tryAcquire(String address) {
		long now = System.currentTimeMillis();
		RequestWindow window = requestsByAddress.get(address);

		if (window == null || window.expiresAt() <= now) {
			if (window == null && requestsByAddress.size() >= MAX_TRACKED_ADDRESSES) {
				removeExpiredWindows(now);
				if (requestsByAddress.size() >= MAX_TRACKED_ADDRESSES) {
					return WINDOW_MILLIS;
				}
			}
			requestsByAddress.put(address, new RequestWindow(now + WINDOW_MILLIS, 1));
			return 0;
		}

		if (window.attempts() >= MAX_ATTEMPTS) {
			return window.expiresAt() - now;
		}

		requestsByAddress.put(address, new RequestWindow(window.expiresAt(), window.attempts() + 1));
		return 0;
	}

	private void removeExpiredWindows(long now) {
		Iterator<RequestWindow> windows = requestsByAddress.values().iterator();
		while (windows.hasNext()) {
			if (windows.next().expiresAt() <= now) {
				windows.remove();
			}
		}
	}

	private record RequestWindow(long expiresAt, int attempts) {
	}
}
