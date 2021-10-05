package com.recomendationapi.model;

import com.recomendationapi.form.DefaultForm;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Entity implements Serializable {

	protected LocalDateTime createdDate;
	protected LocalDateTime updatedDate;
	protected String createdBy;
	protected String updatedBy;
	protected boolean active = false;

	public abstract String getId();

	public abstract void modify(DefaultForm myForm);
}