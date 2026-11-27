package com.bodriopopcorn.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bodriopopcorn.dto.request.TitleRequest;
import com.bodriopopcorn.dto.response.TitleResponse;
import com.bodriopopcorn.entity.TitleType;
import com.bodriopopcorn.service.TitleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

	private final TitleService titleService;

	public TitleController(TitleService titleService) {
		this.titleService = titleService;
	}

	@GetMapping
	public Page<TitleResponse> list(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) Long genreId,
			@RequestParam(required = false) TitleType type,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) BigDecimal minRating,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return titleService.list(search, genreId, type, year, minRating, pageable);
	}

	@GetMapping("/{id}")
	public TitleResponse get(@PathVariable Long id) {
		return titleService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public TitleResponse create(@Valid @RequestBody TitleRequest request) {
		return titleService.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
	public TitleResponse update(@PathVariable Long id, @Valid @RequestBody TitleRequest request) {
		return titleService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		titleService.delete(id);
	}
}
