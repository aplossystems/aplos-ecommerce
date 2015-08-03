package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@ManagedBean
@SessionScoped
@Entity
public class DiscountAllowance extends AplosBean {
	private static final long serialVersionUID = -6597804303797316277L;

	private String name;
	private BigDecimal discountPercentage = new BigDecimal( 0 );

	@Override
	public String getDisplayName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}
}
