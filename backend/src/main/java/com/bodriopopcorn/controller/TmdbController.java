package com.bodriopopcorn.controller;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.bodriopopcorn.dto.request.TmdbReviewRequest;
import com.bodriopopcorn.dto.response.ApiErrorResponse;
import com.bodriopopcorn.dto.response.ReviewResponse;
import com.bodriopopcorn.dto.response.WatchlistResponse;
import com.bodriopopcorn.service.TmdbService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TmdbController.class);
	private final TmdbService service;

	public TmdbController(TmdbService service) {
		this.service = service;
	}

	@GetMapping("/search")
	public JsonNode search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
		return service.search(query, page);
	}

	@GetMapping("/{type}/{id}")
	public Map<String, Object> details(@PathVariable String type, @PathVariable long id) {
		return service.details(type, id);
	}

	@PostMapping("/{type}/{id}/metadata")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void refreshMetadata(@PathVariable String type, @PathVariable long id) {
		service.refreshMetadata(type, id);
	}

	@PostMapping("/{type}/{id}/watchlist")
	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public WatchlistResponse addToWatchlist(Authentication authentication, @PathVariable String type, @PathVariable long id) {
		return service.addToWatchlist(authentication.getName(), type, id);
	}

	@PostMapping("/{type}/{id}/reviews")
	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewResponse addReview(Authentication authentication, @PathVariable String type, @PathVariable long id,
			@Valid @RequestBody TmdbReviewRequest request) {
		return service.addReview(authentication.getName(), type, id, request);
	}

	@ExceptionHandler({RestClientException.class, IllegalStateException.class})
	public ResponseEntity<ApiErrorResponse> providerFailure(Exception exception, HttpServletRequest request) {
		LOGGER.warn("TMDB request failed: {}", exception.getClass().getSimpleName());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiErrorResponse(
				Instant.now(), HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
				"No se pudo consultar TMDB. Inténtalo más tarde.", request.getRequestURI(), Map.of()));
	}
}
