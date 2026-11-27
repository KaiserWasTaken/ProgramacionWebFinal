package com.bodriopopcorn.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.LoginRequest;
import com.bodriopopcorn.dto.request.RegisterRequest;
import com.bodriopopcorn.dto.response.AuthResponse;
import com.bodriopopcorn.entity.RevokedToken;
import com.bodriopopcorn.entity.RoleName;
import com.bodriopopcorn.entity.User;
import com.bodriopopcorn.exception.EmailAlreadyRegisteredException;
import com.bodriopopcorn.repository.RevokedTokenRepository;
import com.bodriopopcorn.repository.RoleRepository;
import com.bodriopopcorn.repository.UserRepository;
import com.bodriopopcorn.security.JwtService;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RevokedTokenRepository revokedTokenRepository;
	private final JwtService jwtService;
	private final EmailService emailService;
	private final NotificationScheduler notificationScheduler;

	public AuthService(
			AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder,
			RoleRepository roleRepository,
			UserRepository userRepository,
			RevokedTokenRepository revokedTokenRepository,
			JwtService jwtService,
			EmailService emailService,
			NotificationScheduler notificationScheduler
	) {
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.revokedTokenRepository = revokedTokenRepository;
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.notificationScheduler = notificationScheduler;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyRegisteredException();
		}

		var userRole = roleRepository.findByName(RoleName.USER)
				.orElseThrow(() -> new IllegalStateException("USER role is missing; apply the database migrations"));
		User user = new User(email, passwordEncoder.encode(request.password()), request.fullName().trim(), userRole);
		userRepository.save(user);
		notifyWelcome(user);
		return issueResponse(user);
	}

	void notifyWelcome(User user) {
		notificationScheduler.afterCommit(
				() -> emailService.sendWelcomeEmail(user.getEmail(), user.getFullName()));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.password()));
		String authenticatedEmail = authentication.getName();
		User user = userRepository.findByEmail(authenticatedEmail)
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		return issueResponse(user);
	}

	@Transactional
	public void logout(String token) {
		var claims = jwtService.parseClaims(token);
		revokedTokenRepository.deleteByExpiresAtBefore(Instant.now());
		revokedTokenRepository.save(new RevokedToken(claims.getId(), claims.getExpiration().toInstant()));
	}

	AuthResponse issueResponse(User user) {
		var issuedToken = jwtService.issueToken(user);
		return new AuthResponse(
				issuedToken.value(),
				"Bearer",
				issuedToken.expiresAt(),
				user.getEmail(),
				user.getRole().getName().name()
		);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
