package com.aplos.ecommerce.beans.playcom;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosAbstractBean;

@Entity
public class PlayMainGenre extends AplosAbstractBean {
	private static final long serialVersionUID = -3063465728402978593L;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlayGender playGender;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySubGenre playSubGenre;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySizeType playSizeType;
	private String name;
	private String genreCode;
	
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

	public PlaySizeType getPlaySizeType() {
		return playSizeType;
	}

	public void setPlaySizeType(PlaySizeType playSizeType) {
		this.playSizeType = playSizeType;
	}

	public String getGenreCode() {
		return genreCode;
	}

	public void setGenreCode(String genreCode) {
		this.genreCode = genreCode;
	}
}
