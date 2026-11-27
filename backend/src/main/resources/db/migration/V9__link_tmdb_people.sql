ALTER TABLE people
    ADD COLUMN tmdb_id BIGINT NULL,
    ADD CONSTRAINT uk_people_tmdb_role UNIQUE (tmdb_id, role_type);
