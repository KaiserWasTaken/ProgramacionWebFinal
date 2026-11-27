package com.bodriopopcorn.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.ReviewRequest;
import com.bodriopopcorn.dto.request.TmdbReviewRequest;
import com.bodriopopcorn.dto.response.ReviewResponse;
import com.bodriopopcorn.dto.response.WatchlistResponse;
import com.bodriopopcorn.entity.Genre;
import com.bodriopopcorn.entity.Person;
import com.bodriopopcorn.entity.PersonRoleType;
import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.entity.TitleCredit;
import com.bodriopopcorn.entity.TitleType;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.GenreRepository;
import com.bodriopopcorn.repository.PersonRepository;
import com.bodriopopcorn.repository.TitleRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TmdbService {

	private final TmdbClient client;
	private final TitleRepository titles;
	private final ReviewService reviews;
	private final WatchlistService watchlist;
	private final GenreRepository genres;
	private final PersonRepository people;

	public TmdbService(TmdbClient client, TitleRepository titles, ReviewService reviews, WatchlistService watchlist,
			GenreRepository genres, PersonRepository people) {
		this.client = client;
		this.titles = titles;
		this.reviews = reviews;
		this.watchlist = watchlist;
		this.genres = genres;
		this.people = people;
	}

	public JsonNode search(String query, int page) {
		return client.search(query, page);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> details(String type, long id) {
		validateReference(type, id);
		JsonNode details = client.details(type, id);
		Long localId = titles.findByTmdbMediaTypeAndTmdbId(type, id).map(Title::getId).orElse(null);
		if (localId == null) {
			return Map.of("title", details);
		}
		return Map.of("title", details, "localTitleId", localId);
	}

	@Transactional
	public WatchlistResponse addToWatchlist(String email, String type, long id) {
		return watchlist.add(email, findOrCreate(type, id).getId());
	}

	@Transactional
	public ReviewResponse addReview(String email, String type, long id, TmdbReviewRequest request) {
		Long titleId = findOrCreate(type, id).getId();
		return reviews.create(email, new ReviewRequest(titleId, request.rating(), request.comment()));
	}

	@Transactional
	public void refreshMetadata(String type, long id) {
		validateReference(type, id);
		Title title = titles.findByTmdbMediaTypeAndTmdbId(type, id)
				.orElseThrow(() -> new ResourceNotFoundException("TMDB title", id));
		if (!needsMetadata(title)) {
			return;
		}
		JsonNode data = client.details(type, id);
		if (data.path("id").asLong() != id) {
			throw new IllegalStateException("TMDB returned invalid title data");
		}
		addMissingMetadata(title, data);
	}

	private Title findOrCreate(String type, long id) {
		validateReference(type, id);
		return titles.findByTmdbMediaTypeAndTmdbId(type, id).orElseGet(() -> create(type, id));
	}

	private Title create(String type, long id) {
		JsonNode data = client.details(type, id);
		String name = data.path(type.equals("movie") ? "title" : "name").asText("").trim();
		String date = data.path(type.equals("movie") ? "release_date" : "first_air_date").asText("");
		if (data.path("id").asLong() != id || name.isEmpty() || name.length() > 180) {
			throw new IllegalStateException("TMDB returned invalid title data");
		}
		int year;
		try {
			year = LocalDate.parse(date).getYear();
		} catch (DateTimeParseException exception) {
			throw new IllegalArgumentException("Este título de TMDB no tiene una fecha de estreno válida.");
		}
		if (year < 1888 || year > 2100) {
			throw new IllegalArgumentException("El año de estreno de este título no es compatible.");
		}
		Title title = new Title(type.equals("movie") ? TitleType.MOVIE : TitleType.SERIES,
				name, data.path("overview").asText(""), year);
		title.setTmdbReference(type, id);
		String poster = data.path("poster_path").asText("");
		if (!poster.isEmpty() && poster.matches("/[A-Za-z0-9._-]+")) {
			title.setPosterUrl("https://image.tmdb.org/t/p/w500" + poster);
		}
		if (type.equals("movie") && data.path("runtime").asInt() > 0) {
			title.setDurationMinutes(data.path("runtime").asInt());
		}
		if (type.equals("tv") && data.path("number_of_seasons").asInt() > 0) {
			title.setSeasons(data.path("number_of_seasons").asInt());
		}
		addMissingMetadata(title, data);
		return titles.saveAndFlush(title);
	}

	private boolean needsMetadata(Title title) {
		return title.getGenres().isEmpty() || title.getCredits().stream()
				.noneMatch(credit -> credit.getPerson().getRoleType() == PersonRoleType.DIRECTOR);
	}

	private void addMissingMetadata(Title title, JsonNode data) {
		if (title.getGenres().isEmpty()) {
			JsonNode tmdbGenres = data.path("genres");
			if (!tmdbGenres.isArray()) {
				throw new IllegalStateException("TMDB returned invalid genres");
			}
			for (JsonNode tmdbGenre : tmdbGenres) {
				String name = tmdbGenre.path("name").asText("").trim();
				if (name.isEmpty() || name.length() > 60) {
					throw new IllegalStateException("TMDB returned an invalid genre name");
				}
				Genre genre = genres.findByName(name).orElseGet(() -> genres.save(new Genre(name)));
				title.getGenres().add(genre);
			}
		}
		if (title.getCredits().stream().noneMatch(credit -> credit.getPerson().getRoleType() == PersonRoleType.DIRECTOR)) {
			JsonNode crew = data.path("credits").path("crew");
			if (!crew.isArray()) {
				throw new IllegalStateException("TMDB returned invalid credits");
			}
			Set<Long> directorIds = new HashSet<>();
			for (JsonNode member : crew) {
				if (!"Director".equals(member.path("job").asText())) {
					continue;
				}
				long personId = member.path("id").asLong();
				String name = member.path("name").asText("").trim();
				if (personId <= 0 || name.isEmpty() || name.length() > 120) {
					throw new IllegalStateException("TMDB returned an invalid director");
				}
				if (directorIds.add(personId)) {
					Person director = people.findByTmdbIdAndRoleType(personId, PersonRoleType.DIRECTOR)
							.orElseGet(() -> people.save(new Person(name, PersonRoleType.DIRECTOR, personId)));
					title.getCredits().add(new TitleCredit(title, director, null));
				}
			}
		}
	}

	private void validateReference(String type, long id) {
		if (!(type.equals("movie") || type.equals("tv")) || id <= 0) {
			throw new IllegalArgumentException("Invalid TMDB title type or ID");
		}
	}
}
