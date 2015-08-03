package com.aplos.ecommerce.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@ManagedBean
@SessionScoped
@Entity
public class SupplierType extends AplosBean {
	private static final long serialVersionUID = 464838628549914379L;

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return name;
	}
}
