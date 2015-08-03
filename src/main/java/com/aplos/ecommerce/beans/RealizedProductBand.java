package com.aplos.ecommerce.beans;

import javax.faces.bean.ManagedBean;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
@ManagedBean
public class RealizedProductBand extends AplosBean {
	private static final long serialVersionUID = 1173087846969723261L;

	private String bandName;

	public String getBandName() {
		return bandName;
	}

	public void setBandName(String bandName) {
		this.bandName = bandName;
	}

	@Override
	public String getDisplayName() {
		return bandName;
	}

}

