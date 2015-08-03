package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.beans.AplosBean;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class AdditionalShippingOption extends AplosBean {
	private static final long serialVersionUID = -7136954535080631293L;

	private String name;
	private BigDecimal cost = new BigDecimal( 0 );
	private String deliveryTime;

	public AdditionalShippingOption() {}

	public AdditionalShippingOption( String name, BigDecimal cost, String deliveryTime ) {
		this.name = name;
		this.cost = cost;
		this.deliveryTime = deliveryTime;
	}

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

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}
}
