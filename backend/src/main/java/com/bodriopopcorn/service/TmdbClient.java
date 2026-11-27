package com.bodriopopcorn.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TmdbClient {

	private final RestClient client;
	private final String accessToken;

	@Autowired
	public TmdbClient(RestClient.Builder builder, @Value("${tmdb.access-token:${TMDB_ACCESS_TOKEN:}}") String accessToken) {
		this(builder.requestFactory(requestFactory()).build(), accessToken);
	}

	public TmdbClient(RestClient client, String accessToken) {
		this.client = client;
		this.accessToken = accessToken;
	}

	public JsonNode search(String query, int page) {
		if (query == null || query.isBlank() || query.length() > 200 || page < 1 || page > 500) {
			throw new IllegalArgumentException("Invalid TMDB search or page");
		}
		JsonNode result = get("https://api.themoviedb.org/3/search/multi?query={query}&language=es-MX&include_adult=false&page={page}",
				query.trim(), page);
		if (!result.path("results").isArray() || !result.path("page").isInt()
				|| !result.path("total_pages").isInt()) {
			throw new IllegalStateException("TMDB returned an invalid search response");
		}
		return result;
	}

	public JsonNode details(String type, long id) {
		if (!("movie".equals(type) || "tv".equals(type)) || id <= 0) {
			throw new IllegalArgumentException("Invalid TMDB title type or ID");
		}
		return get("https://api.themoviedb.org/3/{type}/{id}?language=es-MX&append_to_response=credits",
				type, id);
	}

	private JsonNode get(String uri, Object... variables) {
		if (accessToken == null || accessToken.isBlank()) {
			throw new IllegalStateException("TMDB access token must be configured");
		}
		JsonNode result = client.get()
				.uri(uri, variables)
				.headers(headers -> headers.setBearerAuth(accessToken))
				.retrieve()
				.body(JsonNode.class);
		if (result == null || !result.isObject()) {
			throw new IllegalStateException("TMDB returned an invalid response");
		}
		return result;
	}

	private static SimpleClientHttpRequestFactory requestFactory() {
		var factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Duration.ofSeconds(5));
		factory.setReadTimeout(Duration.ofSeconds(10));
		return factory;
	}
}
