package com.bodriopopcorn.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class GoogleIdentityVerifier {
	private final String clientId;
	private final NimbusJwtDecoder decoder;

	@Autowired
	public GoogleIdentityVerifier(@Value("${google.client-id:${GOOGLE_CLIENT_ID:}}") String clientId) {
		this(clientId, "https://www.googleapis.com/oauth2/v3/certs");
	}

	GoogleIdentityVerifier(String clientId, String jwkSetUri) {
		this.clientId = clientId;
		this.decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		OAuth2TokenValidator<Jwt> claims = jwt -> {
			String issuer = jwt.getClaimAsString("iss");
			boolean valid = ("accounts.google.com".equals(issuer) || "https://accounts.google.com".equals(issuer))
					&& jwt.getAudience() != null && jwt.getAudience().contains(clientId)
					&& (jwt.getAudience().size() == 1 || clientId.equals(jwt.getClaimAsString("azp")))
					&& jwt.getExpiresAt() != null;
			return valid ? OAuth2TokenValidatorResult.success()
					: OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
		};
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(), claims));
	}

	public Identity verify(String credential) {
		if (clientId.isBlank()) throw new IllegalStateException("El acceso con Google no está configurado.");
		try {
			Jwt token = decoder.decode(credential);
			String subject = token.getSubject();
			String email = token.getClaimAsString("email");
			String name = token.getClaimAsString("name");
			if (subject == null || subject.isBlank() || subject.length() > 255 || email == null
					|| email.isBlank() || email.length() > 254 || !Boolean.TRUE.equals(token.getClaimAsBoolean("email_verified"))) {
				throw new BadCredentialsException("La identidad de Google no es válida.");
			}
			email = email.trim().toLowerCase(Locale.ROOT);
			name = name == null || name.isBlank() ? email : name.trim();
			if (name.length() > 120) name = name.substring(0, 120);
			String hostedDomain = token.getClaimAsString("hd");
			boolean authoritativeEmail = email.endsWith("@gmail.com")
					|| (hostedDomain != null && !hostedDomain.isBlank());
			return new Identity(subject, email, name, authoritativeEmail);
		} catch (JwtException exception) {
			throw new BadCredentialsException("La identidad de Google no es válida.", exception);
		}
	}

	public record Identity(String subject, String email, String name, boolean authoritativeEmail) { }
}
