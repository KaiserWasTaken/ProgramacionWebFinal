package com.bodriopopcorn.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "titles")
public class Title {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TitleType type;

	@Column(nullable = false, length = 180)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String synopsis;

	@Column(name = "release_year", nullable = false)
	private Integer releaseYear;

	@Column(name = "duration_minutes")
	private Integer durationMinutes;

	private Integer seasons;

	@Column(name = "poster_url", length = 500)
	private String posterUrl;

	@Column(name = "tmdb_id")
	private Long tmdbId;

	@Column(name = "tmdb_media_type", length = 5)
	private String tmdbMediaType;

	@Column(name = "avg_rating", nullable = false, precision = 3, scale = 2)
	private BigDecimal avgRating = BigDecimal.ZERO;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@BatchSize(size = 50)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "title_genres",
			joinColumns = @JoinColumn(name = "title_id"),
			inverseJoinColumns = @JoinColumn(name = "genre_id")
	)
	private Set<Genre> genres = new HashSet<>();

	@BatchSize(size = 50)
	@OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TitleCredit> credits = new HashSet<>();

	protected Title() {
	}

	public Title(TitleType type, String name, String synopsis, Integer releaseYear) {
		this.type = type;
		this.name = name;
		this.synopsis = synopsis;
		this.releaseYear = releaseYear;
	}

	public Long getId() {
		return id;
	}

	public TitleType getType() {
		return type;
	}

	public void setType(TitleType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Integer getSeasons() {
		return seasons;
	}

	public void setSeasons(Integer seasons) {
		this.seasons = seasons;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public Long getTmdbId() {
		return tmdbId;
	}

	public String getTmdbMediaType() {
		return tmdbMediaType;
	}

	public void setTmdbReference(String mediaType, Long id) {
		this.tmdbMediaType = mediaType;
		this.tmdbId = id;
	}

	public BigDecimal getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(BigDecimal avgRating) {
		this.avgRating = avgRating;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public Set<TitleCredit> getCredits() {
		return credits;
	}
}
