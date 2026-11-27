package com.bodriopopcorn.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.TitleCreditRequest;
import com.bodriopopcorn.dto.request.TitleRequest;
import com.bodriopopcorn.dto.response.GenreResponse;
import com.bodriopopcorn.dto.response.PersonResponse;
import com.bodriopopcorn.dto.response.TitleCreditResponse;
import com.bodriopopcorn.dto.response.TitleResponse;
import com.bodriopopcorn.entity.Genre;
import com.bodriopopcorn.entity.Person;
import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.entity.TitleCredit;
import com.bodriopopcorn.entity.TitleType;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.GenreRepository;
import com.bodriopopcorn.repository.PersonRepository;
import com.bodriopopcorn.repository.TitleRepository;

@Service
public class TitleService {

	private final TitleRepository titleRepository;
	private final GenreRepository genreRepository;
	private final PersonRepository personRepository;

	public TitleService(
			TitleRepository titleRepository,
			GenreRepository genreRepository,
			PersonRepository personRepository
	) {
		this.titleRepository = titleRepository;
		this.genreRepository = genreRepository;
		this.personRepository = personRepository;
	}

	@Transactional(readOnly = true)
	public Page<TitleResponse> list(
			String search,
			Long genreId,
			TitleType type,
			Integer year,
			BigDecimal minRating,
			Pageable pageable
	) {
		PageableSupport.validate(pageable, "id", "type", "name", "releaseYear", "avgRating", "createdAt");
		if (genreId != null && genreId <= 0) {
			throw new IllegalArgumentException("genreId must be positive");
		}
		if (year != null && (year < 1888 || year > 2100)) {
			throw new IllegalArgumentException("year must be between 1888 and 2100");
		}
		if (minRating != null && (minRating.compareTo(BigDecimal.ZERO) < 0
				|| minRating.compareTo(BigDecimal.valueOf(5)) > 0)) {
			throw new IllegalArgumentException("minRating must be between 0 and 5");
		}
		return titleRepository.findAll(
				TitleSpecifications.withFilters(search, genreId, type, year, minRating), pageable)
				.map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public TitleResponse get(Long id) {
		return toResponse(findWithDetails(id));
	}

	@Transactional
	public TitleResponse create(TitleRequest request) {
		Title title = new Title(request.type(), request.name().trim(), request.synopsis().trim(), request.releaseYear());
		apply(title, request);
		return toResponse(titleRepository.save(title));
	}

	@Transactional
	public TitleResponse update(Long id, TitleRequest request) {
		Title title = findWithDetails(id);
		title.setType(request.type());
		title.setName(request.name().trim());
		title.setSynopsis(request.synopsis().trim());
		title.setReleaseYear(request.releaseYear());
		apply(title, request);
		return toResponse(title);
	}

	@Transactional
	public void delete(Long id) {
		titleRepository.delete(findWithDetails(id));
	}

	private void apply(Title title, TitleRequest request) {
		title.setDurationMinutes(request.durationMinutes());
		title.setSeasons(request.seasons());
		title.setPosterUrl(request.posterUrl() == null ? null : request.posterUrl().trim());

		List<Genre> genres = genreRepository.findAllById(request.genreIds()).stream().toList();
		if (genres.size() != request.genreIds().size()) {
			throw new IllegalArgumentException("One or more genreIds do not exist or are duplicated");
		}
		title.getGenres().clear();
		title.getGenres().addAll(genres);

		List<TitleCreditRequest> creditRequests = request.credits() == null ? List.of() : request.credits();
		List<Long> personIds = creditRequests.stream().map(TitleCreditRequest::personId).toList();
		List<Person> people = personRepository.findAllById(personIds).stream().toList();
		if (people.size() != personIds.size()) {
			throw new IllegalArgumentException("One or more credit personIds do not exist or are duplicated");
		}
		Map<Long, Person> peopleById = people.stream().collect(Collectors.toMap(Person::getId, person -> person));
		title.getCredits().clear();
		for (TitleCreditRequest credit : creditRequests) {
			Person person = peopleById.get(credit.personId());
			title.getCredits().add(new TitleCredit(title, person, credit.characterName()));
		}
	}

	private Title findWithDetails(Long id) {
		return titleRepository.findWithDetailsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Title", id));
	}

	private TitleResponse toResponse(Title title) {
		List<GenreResponse> genres = title.getGenres().stream()
				.sorted((first, second) -> first.getName().compareToIgnoreCase(second.getName()))
				.map(genre -> new GenreResponse(genre.getId(), genre.getName()))
				.toList();
		List<TitleCreditResponse> credits = title.getCredits().stream()
				.sorted((first, second) ->
						first.getPerson().getName().compareToIgnoreCase(second.getPerson().getName()))
				.map(credit -> new TitleCreditResponse(
						new PersonResponse(
								credit.getPerson().getId(),
								credit.getPerson().getName(),
								credit.getPerson().getRoleType()),
						credit.getCharacterName()))
				.toList();
		return new TitleResponse(
				title.getId(),
				title.getType(),
				title.getName(),
				title.getSynopsis(),
				title.getReleaseYear(),
				title.getDurationMinutes(),
				title.getSeasons(),
				title.getPosterUrl(),
				title.getAvgRating(),
				genres,
				credits,
				title.getCreatedAt(),
				title.getTmdbMediaType(),
				title.getTmdbId()
		);
	}
}
