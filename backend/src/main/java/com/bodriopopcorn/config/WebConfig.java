package com.bodriopopcorn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final PasswordResetRateLimitInterceptor passwordResetRateLimitInterceptor;

	public WebConfig(PasswordResetRateLimitInterceptor passwordResetRateLimitInterceptor) {
		this.passwordResetRateLimitInterceptor = passwordResetRateLimitInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passwordResetRateLimitInterceptor)
				.addPathPatterns("/api/auth/forgot-password");
	}
}
