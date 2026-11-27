package com.bodriopopcorn.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.entity.PasswordResetToken;
import com.bodriopopcorn.repository.PasswordResetTokenRepository;
import com.bodriopopcorn.repository.UserRepository;
import com.bodriopopcorn.exception.TooManyRequestsException;

@Service
public class PasswordResetService {

	private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(30);
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final PasswordResetTokenRepository tokenRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final SmsService smsService;
	private final NotificationScheduler notificationScheduler;
	private final PasswordResetRateLimiter rateLimiter;
	private final String frontendUrl;

	public PasswordResetService(
			PasswordResetTokenRepository tokenRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			EmailService emailService,
			SmsService smsService,
			NotificationScheduler notificationScheduler,
			PasswordResetRateLimiter rateLimiter,
			@Value("${app.frontend-url:http://localhost:5173}") String frontendUrl
	) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.smsService = smsService;
		this.notificationScheduler = notificationScheduler;
		this.rateLimiter = rateLimiter;
		this.frontendUrl = frontendUrl;
	}

	@Transactional
	public void requestReset(String email) {
		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
		long retryAfterMillis = rateLimiter.tryAcquire("email:" + hashToken(normalizedEmail));
		if (retryAfterMillis > 0) {
			throw new TooManyRequestsException(Math.max(1, (retryAfterMillis + 999) / 1000));
		}

		Instant now = Instant.now();
		tokenRepository.deleteByExpiresAtBefore(now);
		userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
			tokenRepository.deleteByUser_IdAndUsedFalse(user.getId());
			String rawToken = createToken();
			tokenRepository.save(new PasswordResetToken(
					user, hashToken(rawToken), now.plus(TOKEN_LIFETIME)));
			String resetLink = frontendUrl.replaceAll("/+$", "")
					+ "/reset-password?token="
					+ URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
			notificationScheduler.afterCommit(
					() -> emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink));
		});
	}

	@Transactional
	public void resetPassword(String rawToken, String newPassword) {
		PasswordResetToken resetToken = tokenRepository
				.findByTokenHashAndUsedFalse(hashToken(rawToken))
				.filter(token -> token.getExpiresAt().isAfter(Instant.now()))
				.orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

		var user = resetToken.getUser();
		user.setPasswordHash(passwordEncoder.encode(newPassword));
		resetToken.markUsed();
		userRepository.save(user);
		tokenRepository.save(resetToken);
		notificationScheduler.afterCommit(() -> smsService.sendPasswordChangedNotice(user.getPhone()));
	}

	private String createToken() {
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hashToken(String rawToken) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available.", exception);
		}
	}
}
