package com.aplos.ecommerce.beans.playcom;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosAbstractBean;

@Entity
public class PlaySize extends AplosAbstractBean {
	private static final long serialVersionUID = -2041214502721961774L;
	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySizeType playSizeType;
	
	public String getDisplayName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlaySizeType getPlaySizeType() {
		return playSizeType;
	}

	public void setPlaySizeType(PlaySizeType playSizeType) {
		this.playSizeType = playSizeType;
	}
}
