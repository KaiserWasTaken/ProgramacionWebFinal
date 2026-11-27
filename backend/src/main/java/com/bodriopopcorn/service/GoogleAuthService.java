package com.bodriopopcorn.service;

import java.util.UUID;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.response.AuthResponse;
import com.bodriopopcorn.entity.RoleName;
import com.bodriopopcorn.entity.User;
import com.bodriopopcorn.repository.RoleRepository;
import com.bodriopopcorn.repository.UserRepository;

@Service
public class GoogleAuthService {
	private final UserRepository users;
	private final RoleRepository roles;
	private final PasswordEncoder passwords;
	private final AuthService auth;
	private final GoogleIdentityVerifier verifier;

	public GoogleAuthService(UserRepository users, RoleRepository roles, PasswordEncoder passwords,
			AuthService auth, GoogleIdentityVerifier verifier) {
		this.users = users;
		this.roles = roles;
		this.passwords = passwords;
		this.auth = auth;
		this.verifier = verifier;
	}

	@Transactional
	public AuthResponse login(String credential) {
		var identity = verifier.verify(credential);
		User user = users.findByGoogleSubject(identity.subject()).orElse(null);
		if (user == null) {
			if (users.existsByEmail(identity.email())) {
				throw new IllegalArgumentException("Esta cuenta ya existe. Inicia sesión con tu contraseña y vincula Google en tu perfil.");
			}
			if (!identity.authoritativeEmail()) {
				throw new IllegalArgumentException("Regístrate con tu correo y contraseña y vincula Google desde tu perfil.");
			}
			var role = roles.findByName(RoleName.USER)
					.orElseThrow(() -> new IllegalStateException("USER role is missing; apply the database migrations"));
			user = new User(identity.email(), passwords.encode(UUID.randomUUID().toString()), identity.name(), role);
			user.setGoogleSubject(identity.subject());
			users.save(user);
			auth.notifyWelcome(user);
		}
		if (!user.isEnabled()) throw new DisabledException("Account disabled");
		return auth.issueResponse(user);
	}

	@Transactional
	public void link(String email, String credential) {
		var identity = verifier.verify(credential);
		User user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
		if (user.getGoogleSubject() != null && !user.getGoogleSubject().equals(identity.subject())) {
			throw new IllegalArgumentException("Esta cuenta ya está vinculada a otra cuenta de Google.");
		}
		users.findByGoogleSubject(identity.subject()).ifPresent(existing -> {
			if (!existing.getId().equals(user.getId())) {
				throw new IllegalArgumentException("Esta cuenta de Google ya está vinculada a otro usuario.");
			}
		});
		user.setGoogleSubject(identity.subject());
	}
}
