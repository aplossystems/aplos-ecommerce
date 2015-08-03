package com.aplos.ecommerce.interfaces;

import com.aplos.ecommerce.beans.RealizedProduct;

public abstract interface RealizedProductRetriever {
	public abstract Long getId();
	public abstract RealizedProduct retrieveRealizedProduct(RealizedProduct product);
	public abstract String getDisplayName();

}
