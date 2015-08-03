package com.aplos.ecommerce.beans.playcom;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosAbstractBean;

@Entity
public class PlayColour extends AplosAbstractBean {
	private static final long serialVersionUID = -4107936333423550738L;
	
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
