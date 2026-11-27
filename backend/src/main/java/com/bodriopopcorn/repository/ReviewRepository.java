package com.bodriopopcorn.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodriopopcorn.entity.Review;
import com.bodriopopcorn.entity.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

	@EntityGraph(attributePaths = {"user", "title"})
	Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

	@EntityGraph(attributePaths = {"user", "title"})
	Page<Review> findByUser_Id(Long userId, Pageable pageable);

	@EntityGraph(attributePaths = {"user", "title"})
	Page<Review> findByTitle_IdAndStatus(Long titleId, ReviewStatus status, Pageable pageable);

	Optional<Review> findByUser_IdAndTitle_Id(Long userId, Long titleId);

	boolean existsByUser_IdAndTitle_Id(Long userId, Long titleId);

	void deleteByUser_Id(Long userId);

	@Query("select distinct r.title.id from Review r where r.user.id = :userId and r.status = :status")
	List<Long> findDistinctTitleIdsByUserIdAndStatus(
			@Param("userId") Long userId,
			@Param("status") ReviewStatus status
	);

	@Query("select avg(r.rating) from Review r where r.title.id = :titleId and r.status = :status")
	Double findAverageRatingByTitleIdAndStatus(
			@Param("titleId") Long titleId,
			@Param("status") ReviewStatus status
	);

	@Override
	@EntityGraph(attributePaths = {"user", "title"})
	Page<Review> findAll(Specification<Review> specification, Pageable pageable);
}
