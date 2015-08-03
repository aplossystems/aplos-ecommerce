package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.couriers.ShippingBox;

@Entity
public class ShippingBoxOrder extends AplosBean {
	private static final long serialVersionUID = 3716007494516341937L;

	@ManyToOne
	private ShippingBox shippingBox;
	private int quantity;

	@Override
	public String getDisplayName() {
		if( shippingBox != null ) {
			return shippingBox.getName();
		} else {
			return super.getDisplayName();
		}
	}

	public int calculateTotalVolume() {
		if( getShippingBox() == null ) {
			return 0;
		} else {
			return getShippingBox().calculateVolume() * getQuantity();
		}
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setShippingBox(ShippingBox shippingBox) {
		this.shippingBox = shippingBox;
	}
	public ShippingBox getShippingBox() {
		return shippingBox;
	}
}
