package com.bodriopopcorn.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bodriopopcorn.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

	private final SecretKey signingKey;

	public JwtService(@Value("${jwt.secret}") String secret) {
		if (secret == null || secret.isBlank()) {
			throw new IllegalStateException("Set JWT_SECRET to a Base64-encoded key with at least 32 bytes");
		}
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}

	public IssuedToken issueToken(User user) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME);
		String tokenId = UUID.randomUUID().toString();
		String token = Jwts.builder()
				.subject(user.getEmail())
				.id(tokenId)
				.claim("role", user.getRole().getName().name())
				.issuedAt(Date.from(issuedAt))
				.expiration(Date.from(expiresAt))
				.signWith(signingKey, Jwts.SIG.HS256)
				.compact();
		return new IssuedToken(token, tokenId, expiresAt);
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public record IssuedToken(String value, String id, Instant expiresAt) {
	}
}
