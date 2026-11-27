package com.bodriopopcorn.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bodriopopcorn.dto.request.ForgotPasswordRequest;
import com.bodriopopcorn.dto.request.GoogleCredentialRequest;
import com.bodriopopcorn.dto.request.LoginRequest;
import com.bodriopopcorn.dto.request.RegisterRequest;
import com.bodriopopcorn.dto.request.ResetPasswordRequest;
import com.bodriopopcorn.dto.response.AuthResponse;
import com.bodriopopcorn.dto.response.CurrentUserResponse;
import com.bodriopopcorn.dto.response.MessageResponse;
import com.bodriopopcorn.service.AuthService;
import com.bodriopopcorn.service.GoogleAuthService;
import com.bodriopopcorn.service.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final PasswordResetService passwordResetService;
	private final GoogleAuthService googleAuthService;

	public AuthController(AuthService authService, PasswordResetService passwordResetService, GoogleAuthService googleAuthService) {
		this.authService = authService;
		this.passwordResetService = passwordResetService;
		this.googleAuthService = googleAuthService;
	}

	@PostMapping("/google")
	public AuthResponse google(@Valid @RequestBody GoogleCredentialRequest request) {
		return googleAuthService.login(request.credential());
	}

	@PostMapping("/google/link")
	public ResponseEntity<Void> linkGoogle(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody GoogleCredentialRequest request) {
		googleAuthService.link(userDetails.getUsername(), request.credential());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(201).body(authService.register(request));
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/forgot-password")
	public MessageResponse requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
		passwordResetService.requestReset(request.email());
		return new MessageResponse("If the account exists, password reset instructions will be sent.");
	}

	@PostMapping("/reset-password")
	public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		passwordResetService.resetPassword(request.token(), request.password());
		return new MessageResponse("Password reset successfully.");
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		authService.logout(authorization.substring(7));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public CurrentUserResponse currentUser(@AuthenticationPrincipal UserDetails userDetails) {
		String role = userDetails.getAuthorities().iterator().next().getAuthority().substring("ROLE_".length());
		return new CurrentUserResponse(userDetails.getUsername(), role);
	}
}
