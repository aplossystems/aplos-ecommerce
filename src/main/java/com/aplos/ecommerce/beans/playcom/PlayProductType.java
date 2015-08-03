package com.aplos.ecommerce.beans.playcom;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosAbstractBean;

@Entity
public class PlayProductType extends AplosAbstractBean {
	private static final long serialVersionUID = -3063465728402978593L;
	
	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlayGender playGender;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySubGenre playSubGenre;
	
	public String getDisplayName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayGender getPlayGender() {
		return playGender;
	}

	public void setPlayGender(PlayGender playGender) {
		this.playGender = playGender;
	}

	public PlaySubGenre getPlaySubGenre() {
		return playSubGenre;
	}

	public void setPlaySubGenre(PlaySubGenre playSubGenre) {
		this.playSubGenre = playSubGenre;
	}
}
