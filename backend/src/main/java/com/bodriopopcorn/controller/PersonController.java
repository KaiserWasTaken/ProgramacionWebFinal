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

import com.bodriopopcorn.dto.request.PersonRequest;
import com.bodriopopcorn.dto.response.PersonResponse;
import com.bodriopopcorn.entity.PersonRoleType;
import com.bodriopopcorn.service.PersonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/people")
public class PersonController {

	private final PersonService personService;

	public PersonController(PersonService personService) {
		this.personService = personService;
	}

	@GetMapping
	public Page<PersonResponse> list(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) PersonRoleType roleType,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return personService.list(search, roleType, pageable);
	}

	@GetMapping("/{id}")
	public PersonResponse get(@PathVariable Long id) {
		return personService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public PersonResponse create(@Valid @RequestBody PersonRequest request) {
		return personService.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
		return personService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		personService.delete(id);
	}
}
