package com.bodriopopcorn.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bodriopopcorn.exception.TooManyRequestsException;
import com.bodriopopcorn.service.PasswordResetRateLimiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PasswordResetRateLimitInterceptor implements HandlerInterceptor {

	private final PasswordResetRateLimiter rateLimiter;

	public PasswordResetRateLimitInterceptor(PasswordResetRateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler
	) {
		if (!"POST".equals(request.getMethod())) {
			return true;
		}

		long retryAfterMillis = rateLimiter.tryAcquire(request.getRemoteAddr());
		if (retryAfterMillis <= 0) {
			return true;
		}

		long retryAfterSeconds = Math.max(1, (retryAfterMillis + 999) / 1000);
		throw new TooManyRequestsException(retryAfterSeconds);
	}
}
