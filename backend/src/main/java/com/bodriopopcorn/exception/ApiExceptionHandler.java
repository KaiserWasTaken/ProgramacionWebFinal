package com.bodriopopcorn.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.bodriopopcorn.dto.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiErrorResponse> handleUploadTooLarge(MaxUploadSizeExceededException exception,
			HttpServletRequest request) {
		return error(HttpStatus.PAYLOAD_TOO_LARGE, "La imagen supera el tamaño permitido.", request, Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
	) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(error ->
				fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return error(HttpStatus.BAD_REQUEST, "Request validation failed", request, fieldErrors);
	}

	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(
			EmailAlreadyRegisteredException exception,
			HttpServletRequest request
	) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request,
				Map.of("email", exception.getMessage()));
	}

	@ExceptionHandler(TooManyRequestsException.class)
	public ResponseEntity<ApiErrorResponse> handleTooManyRequests(
			TooManyRequestsException exception,
			HttpServletRequest request
	) {
		ResponseEntity<ApiErrorResponse> error = error(
				HttpStatus.TOO_MANY_REQUESTS, exception.getMessage(), request, Map.of());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.header(HttpHeaders.RETRY_AFTER, Long.toString(exception.getRetryAfterSeconds()))
				.body(error.getBody());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthentication(
			AuthenticationException exception,
			HttpServletRequest request
	) {
		if (request.getRequestURI().equals("/api/auth/google/link")) {
			return error(HttpStatus.BAD_REQUEST, "No se pudo validar la cuenta de Google.", request, Map.of());
		}
		String message = request.getRequestURI().startsWith("/api/auth/google")
				? "No se pudo validar la cuenta de Google."
				: "Invalid email or password";
		return error(HttpStatus.UNAUTHORIZED, message, request, Map.of());
	}

	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthenticationServiceFailure(
			InternalAuthenticationServiceException exception,
			HttpServletRequest request
	) {
		LOGGER.error("Authentication service failed at {}", request.getRequestURI(), exception);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication service is unavailable", request, Map.of());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(
			AccessDeniedException exception,
			HttpServletRequest request
	) {
		return error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request, Map.of());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(
			NoResourceFoundException exception,
			HttpServletRequest request
	) {
		return error(HttpStatus.NOT_FOUND, "Resource not found", request, Map.of());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleDomainNotFound(
			ResourceNotFoundException exception,
			HttpServletRequest request
	) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler({
			IllegalArgumentException.class,
			MethodArgumentTypeMismatchException.class,
			DataIntegrityViolationException.class
	})
	public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
			Exception exception,
			HttpServletRequest request
	) {
		String message = exception instanceof DataIntegrityViolationException
				? "The request conflicts with existing data"
				: exception.getMessage();
		return error(HttpStatus.BAD_REQUEST, message, request, Map.of());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleMalformedJson(
			HttpMessageNotReadableException exception,
			HttpServletRequest request
	) {
		return error(HttpStatus.BAD_REQUEST, "Request body contains invalid JSON", request, Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(
			Exception exception,
			HttpServletRequest request
	) {
		LOGGER.error("Unhandled API error at {}", request.getRequestURI(), exception);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, Map.of());
	}

	private ResponseEntity<ApiErrorResponse> error(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			Map<String, String> fieldErrors
	) {
		return ResponseEntity.status(status).body(new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				fieldErrors
		));
	}
}
