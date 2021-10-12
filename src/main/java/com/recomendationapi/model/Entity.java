package com.recomendationapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recomendationapi.form.DefaultForm;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Entity implements Serializable {

	@JsonIgnore
	protected LocalDateTime createdDate;
	@JsonIgnore
	protected LocalDateTime updatedDate;
	@JsonIgnore
	protected String createdBy;
	@JsonIgnore
	protected String updatedBy;
	protected boolean active = false;

	public abstract String getId();

	public abstract void modify(DefaultForm myForm);
}