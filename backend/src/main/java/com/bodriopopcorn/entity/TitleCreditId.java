package com.bodriopopcorn.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TitleCreditId implements Serializable {

	@Column(name = "title_id")
	private Long titleId;

	@Column(name = "person_id")
	private Long personId;

	public TitleCreditId() {
	}

	public TitleCreditId(Long titleId, Long personId) {
		this.titleId = titleId;
		this.personId = personId;
	}

	public Long getTitleId() {
		return titleId;
	}

	public Long getPersonId() {
		return personId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TitleCreditId that)) {
			return false;
		}
		return Objects.equals(titleId, that.titleId) && Objects.equals(personId, that.personId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(titleId, personId);
	}
}
