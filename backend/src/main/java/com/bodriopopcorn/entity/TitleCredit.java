package com.bodriopopcorn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "title_cast")
public class TitleCredit {

	@EmbeddedId
	private TitleCreditId id;

	@MapsId("titleId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "title_id", nullable = false)
	private Title title;

	@MapsId("personId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	@Column(name = "character_name", length = 120)
	private String characterName;

	protected TitleCredit() {
	}

	public TitleCredit(Title title, Person person, String characterName) {
		this.id = new TitleCreditId();
		this.title = title;
		this.person = person;
		this.characterName = characterName;
	}

	public Person getPerson() {
		return person;
	}

	public String getCharacterName() {
		return characterName;
	}
}
