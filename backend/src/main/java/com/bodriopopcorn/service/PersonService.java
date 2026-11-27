package com.bodriopopcorn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.PersonRequest;
import com.bodriopopcorn.dto.response.PersonResponse;
import com.bodriopopcorn.entity.Person;
import com.bodriopopcorn.entity.PersonRoleType;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.PersonRepository;

@Service
public class PersonService {

	private final PersonRepository personRepository;

	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Transactional(readOnly = true)
	public Page<PersonResponse> list(String search, PersonRoleType roleType, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "name", "roleType");
		return personRepository.findAll(PersonSpecifications.withFilters(search, roleType), pageable)
				.map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public PersonResponse get(Long id) {
		return toResponse(find(id));
	}

	@Transactional
	public PersonResponse create(PersonRequest request) {
		Person person = new Person(request.name().trim(), request.roleType());
		return toResponse(personRepository.save(person));
	}

	@Transactional
	public PersonResponse update(Long id, PersonRequest request) {
		Person person = find(id);
		person.setName(request.name().trim());
		person.setRoleType(request.roleType());
		return toResponse(person);
	}

	@Transactional
	public void delete(Long id) {
		personRepository.delete(find(id));
	}

	private Person find(Long id) {
		return personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Person", id));
	}

	private PersonResponse toResponse(Person person) {
		return new PersonResponse(person.getId(), person.getName(), person.getRoleType());
	}
}
