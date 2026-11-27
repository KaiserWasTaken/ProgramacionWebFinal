package com.bodriopopcorn.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bodriopopcorn.exception.ApiErrorWriter;
import com.bodriopopcorn.repository.RevokedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	private final RevokedTokenRepository revokedTokenRepository;
	private final ApiErrorWriter apiErrorWriter;

	public JwtAuthenticationFilter(
			JwtService jwtService,
			CustomUserDetailsService userDetailsService,
			RevokedTokenRepository revokedTokenRepository,
			ApiErrorWriter apiErrorWriter
	) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.revokedTokenRepository = revokedTokenRepository;
		this.apiErrorWriter = apiErrorWriter;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		Claims claims;
		UserDetails userDetails;
		try {
			claims = jwtService.parseClaims(authorization.substring(7));
			if (claims.getId() == null || claims.getSubject() == null) {
				throw new JwtException("JWT is missing required claims");
			}
			if (revokedTokenRepository.existsById(claims.getId())) {
				apiErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED, "Token has been revoked");
				return;
			}
			userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
		} catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
			SecurityContextHolder.clearContext();
			apiErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED, "Invalid or expired bearer token");
			return;
		} catch (DataAccessException exception) {
			LOGGER.error("Database failure while authenticating request at {}", request.getRequestURI(), exception);
			apiErrorWriter.write(request, response, HttpStatus.INTERNAL_SERVER_ERROR,
					"Authentication service is unavailable");
			return;
		}

		if (!userDetails.isEnabled()) {
			apiErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED, "User account is disabled");
			return;
		}

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}
