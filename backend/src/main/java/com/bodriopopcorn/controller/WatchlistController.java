package com.bodriopopcorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bodriopopcorn.dto.request.WatchlistRequest;
import com.bodriopopcorn.dto.response.WatchlistResponse;
import com.bodriopopcorn.service.WatchlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/watchlist")
@PreAuthorize("hasRole('USER')")
public class WatchlistController {

	private final WatchlistService watchlistService;

	public WatchlistController(WatchlistService watchlistService) {
		this.watchlistService = watchlistService;
	}

	@GetMapping
	public Page<WatchlistResponse> list(
			Authentication authentication,
			@PageableDefault(size = 20, sort = "addedAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return watchlistService.list(authentication.getName(), pageable);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public WatchlistResponse add(Authentication authentication, @Valid @RequestBody WatchlistRequest request) {
		return watchlistService.add(authentication.getName(), request.titleId());
	}

	@DeleteMapping("/{titleId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remove(Authentication authentication, @PathVariable Long titleId) {
		watchlistService.remove(authentication.getName(), titleId);
	}
}
