package com.aplos.ecommerce.beans.playcom;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosAbstractBean;

@Entity
public class PlaySubGenre extends AplosAbstractBean {
	private static final long serialVersionUID = -3063465728402978593L;
	
	private String name;
	
	public String getDisplayName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
