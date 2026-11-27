package com.bodriopopcorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bodriopopcorn.entity.Person;
import com.bodriopopcorn.entity.PersonRoleType;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

	Optional<Person> findByTmdbIdAndRoleType(Long tmdbId, PersonRoleType roleType);
}
