package com.bodriopopcorn.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.GenreRequest;
import com.bodriopopcorn.dto.response.GenreResponse;
import com.bodriopopcorn.entity.Genre;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.GenreRepository;

@Service
public class GenreService {

	private final GenreRepository genreRepository;

	public GenreService(GenreRepository genreRepository) {
		this.genreRepository = genreRepository;
	}

	@Transactional(readOnly = true)
	public Page<GenreResponse> list(String search, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "name");
		String query = search == null ? "" : search.trim();
		return genreRepository.findByNameContainingIgnoreCase(query, pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public GenreResponse get(Long id) {
		return toResponse(find(id));
	}

	@Transactional
	public GenreResponse create(GenreRequest request) {
		String name = request.name().trim();
		ensureNameAvailable(name, null);
		return toResponse(genreRepository.save(new Genre(name)));
	}

	@Transactional
	public GenreResponse update(Long id, GenreRequest request) {
		Genre genre = find(id);
		String name = request.name().trim();
		ensureNameAvailable(name, id);
		genre.setName(name);
		return toResponse(genre);
	}

	@Transactional
	public void delete(Long id) {
		genreRepository.delete(find(id));
	}

	private Genre find(Long id) {
		return genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Genre", id));
	}

	private void ensureNameAvailable(String name, Long currentId) {
		genreRepository.findByName(name)
				.filter(existing -> !Objects.equals(existing.getId(), currentId))
				.ifPresent(existing -> {
					throw new IllegalArgumentException("A genre with this name already exists");
				});
	}

	private GenreResponse toResponse(Genre genre) {
		return new GenreResponse(genre.getId(), genre.getName());
	}
}
