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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bodriopopcorn.dto.request.ModerationRequest;
import com.bodriopopcorn.dto.request.ReviewRequest;
import com.bodriopopcorn.dto.request.ReviewUpdateRequest;
import com.bodriopopcorn.dto.response.ReviewResponse;
import com.bodriopopcorn.entity.ReviewStatus;
import com.bodriopopcorn.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping("/reviews")
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
	public Page<ReviewResponse> listForModeration(
			@RequestParam(required = false) ReviewStatus status,
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return reviewService.listForModeration(status, search, pageable);
	}

	@GetMapping("/reviews/mine")
	@PreAuthorize("hasRole('USER')")
	public Page<ReviewResponse> listMine(
			Authentication authentication,
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return reviewService.listMine(authentication.getName(), pageable);
	}

	@GetMapping("/reviews/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
	public ReviewResponse get(@PathVariable Long id, Authentication authentication) {
		return reviewService.get(id, authentication.getName(), hasPrivilegedRole(authentication));
	}

	@GetMapping("/moderator/reviews/pending")
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
	public Page<ReviewResponse> listPending(
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return reviewService.listForModeration(ReviewStatus.PENDING, null, pageable);
	}

	@GetMapping("/titles/{titleId}/reviews")
	public Page<ReviewResponse> listApprovedForTitle(
			@PathVariable Long titleId,
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return reviewService.listApprovedForTitle(titleId, pageable);
	}

	@PostMapping("/reviews")
	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewResponse create(Authentication authentication, @Valid @RequestBody ReviewRequest request) {
		return reviewService.create(authentication.getName(), request);
	}

	@PutMapping("/reviews/{id}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ReviewResponse update(
			@PathVariable Long id,
			Authentication authentication,
			@Valid @RequestBody ReviewUpdateRequest request
	) {
		return reviewService.update(id, authentication.getName(), hasRole(authentication, "ROLE_ADMIN"), request);
	}

	@PatchMapping("/reviews/{id}/status")
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
	public ReviewResponse moderate(
			@PathVariable Long id,
			@Valid @RequestBody ModerationRequest request
	) {
		return reviewService.moderate(id, request);
	}

	@DeleteMapping("/reviews/{id}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id, Authentication authentication) {
		reviewService.delete(id, authentication.getName(), hasRole(authentication, "ROLE_ADMIN"));
	}

	private boolean hasRole(Authentication authentication, String role) {
		return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role));
	}

	private boolean hasPrivilegedRole(Authentication authentication) {
		return hasRole(authentication, "ROLE_ADMIN") || hasRole(authentication, "ROLE_MODERATOR");
	}
}
