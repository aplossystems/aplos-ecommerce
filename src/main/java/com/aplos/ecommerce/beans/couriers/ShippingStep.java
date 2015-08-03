package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
public class ShippingStep extends AplosBean {
	private static final long serialVersionUID = -97732301303743420L;

	private BigDecimal maxWeight = new BigDecimal( 0 );
	private BigDecimal cost = new BigDecimal( 0 );

	public ShippingStep() {}

	public ShippingStep( BigDecimal maxWeight, BigDecimal cost ) {
		this.maxWeight = maxWeight;
		this.cost = cost;
	}

	public void setMaxWeight(BigDecimal maxWeight) {
		this.maxWeight = maxWeight;
	}

	public BigDecimal getMaxWeight() {
		return maxWeight;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getCost() {
		return cost;
	}
}
