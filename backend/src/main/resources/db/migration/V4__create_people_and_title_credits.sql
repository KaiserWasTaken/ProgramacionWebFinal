CREATE TABLE people (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    role_type VARCHAR(10) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_people_name (name),
    CONSTRAINT chk_people_role_type CHECK (role_type IN ('ACTOR', 'DIRECTOR'))
) ENGINE=InnoDB;

CREATE TABLE title_credits (
    title_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    character_name VARCHAR(120),
    PRIMARY KEY (title_id, person_id),
    KEY idx_title_credits_person_id (person_id),
    CONSTRAINT fk_title_credits_title FOREIGN KEY (title_id) REFERENCES titles (id) ON DELETE CASCADE,
    CONSTRAINT fk_title_credits_person FOREIGN KEY (person_id) REFERENCES people (id) ON DELETE CASCADE
) ENGINE=InnoDB;
