package com.bodriopopcorn.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bodriopopcorn.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

	Optional<Genre> findByName(String name);

	Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
