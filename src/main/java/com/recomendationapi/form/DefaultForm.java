package com.recomendationapi.form;

import com.recomendationapi.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class DefaultForm {

    String id;
    boolean active;

	public Entity convertToEntity() {
	    return null;
    }
}