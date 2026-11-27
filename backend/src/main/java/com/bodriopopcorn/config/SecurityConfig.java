package com.bodriopopcorn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bodriopopcorn.exception.ApiErrorWriter;
import com.bodriopopcorn.repository.RevokedTokenRepository;
import com.bodriopopcorn.security.CustomUserDetailsService;
import com.bodriopopcorn.security.JwtAuthenticationFilter;
import com.bodriopopcorn.security.JwtService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder
	) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			CustomUserDetailsService userDetailsService,
			RevokedTokenRepository revokedTokenRepository,
			ApiErrorWriter apiErrorWriter
	) throws Exception {
		AuthenticationEntryPoint authenticationEntryPoint = (request, response, exception) ->
				apiErrorWriter.write(request, response, org.springframework.http.HttpStatus.UNAUTHORIZED,
						"Authentication is required");
		AccessDeniedHandler accessDeniedHandler = (request, response, exception) ->
				apiErrorWriter.write(request, response, org.springframework.http.HttpStatus.FORBIDDEN,
						"You do not have permission to access this resource");

		http
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationManager(authenticationManager)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/google",
								"/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
						.requestMatchers("/api/auth/logout", "/api/auth/me").authenticated()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().permitAll())
				.addFilterBefore(new JwtAuthenticationFilter(
						jwtService, userDetailsService, revokedTokenRepository, apiErrorWriter),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
