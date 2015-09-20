package com.gpw.radar.domain.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "FILLED_DATA_STATUS")
public class FillDataStatus {

	@Id
	@Column(name = "data_type")
	@Enumerated(EnumType.STRING)
	private Type type;
	private boolean filled;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}
}
