package com.bodriopopcorn.controller;

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

import com.bodriopopcorn.dto.request.GenreRequest;
import com.bodriopopcorn.dto.response.GenreResponse;
import com.bodriopopcorn.service.GenreService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

	private final GenreService genreService;

	public GenreController(GenreService genreService) {
		this.genreService = genreService;
	}

	@GetMapping
	public Page<GenreResponse> list(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return genreService.list(search, pageable);
	}

	@GetMapping("/{id}")
	public GenreResponse get(@PathVariable Long id) {
		return genreService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public GenreResponse create(@Valid @RequestBody GenreRequest request) {
		return genreService.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public GenreResponse update(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
		return genreService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		genreService.delete(id);
	}
}
