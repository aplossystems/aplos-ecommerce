package com.aplos.ecommerce.beans.couriers;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.ShoppingCart;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class ShippingSet extends AplosBean {

	private static final long serialVersionUID = 1065414330910770758L;

	public abstract double calculateCharge(ShoppingCart shoppingCart);
	public abstract String getChargeString(ShoppingCart shoppingCart);
	public abstract double getConvertedCharge(ShoppingCart shoppingCart);
	public abstract String getConvertedChargeString(ShoppingCart shoppingCart);
	@Override
	public abstract String getDisplayName();
	public abstract CourierService getCourierService();

}
