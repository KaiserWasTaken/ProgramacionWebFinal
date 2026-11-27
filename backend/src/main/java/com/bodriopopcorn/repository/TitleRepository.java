package com.bodriopopcorn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.bodriopopcorn.entity.Title;

public interface TitleRepository extends JpaRepository<Title, Long>, JpaSpecificationExecutor<Title> {

	List<Title> findDistinctByGenres_Id(Long genreId);

	Optional<Title> findByTmdbMediaTypeAndTmdbId(String tmdbMediaType, Long tmdbId);

	@EntityGraph(attributePaths = {"genres", "credits.person"})
	@Query("select distinct t from Title t where t.id = :id")
	Optional<Title> findWithDetailsById(@Param("id") Long id);

	@Override
	Page<Title> findAll(Specification<Title> specification, Pageable pageable);
}
