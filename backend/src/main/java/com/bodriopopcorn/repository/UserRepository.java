package com.bodriopopcorn.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bodriopopcorn.entity.RoleName;
import com.bodriopopcorn.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findByEmail(String email);
	Optional<User> findByGoogleSubject(String googleSubject);

	boolean existsByEmail(String email);

	long countByRole_NameAndEnabledTrue(RoleName roleName);

	@Override
	@EntityGraph(attributePaths = "role")
	Page<User> findAll(Specification<User> specification, Pageable pageable);
}
