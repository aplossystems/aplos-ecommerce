package com.aplos.ecommerce.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
@ManagedBean
@SessionScoped
@Cache
public class PaymentMethod extends AplosBean implements PositionedBean {
	private static final long serialVersionUID = -1161242606998319110L;

	private String name;
	private Integer positionIdx;
	private boolean isSystemPaymentRequired = false;
	private boolean isVisibleFrontend = true;

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

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public boolean isSystemPaymentRequired() {
		return isSystemPaymentRequired;
	}

	public void setSystemPaymentRequired(boolean isSystemPaymentRequired) {
		this.isSystemPaymentRequired = isSystemPaymentRequired;
	}

	public boolean isVisibleFrontend() {
		return isVisibleFrontend;
	}

	public void setVisibleFrontend(boolean isVisibleFrontend) {
		this.isVisibleFrontend = isVisibleFrontend;
	}

}
