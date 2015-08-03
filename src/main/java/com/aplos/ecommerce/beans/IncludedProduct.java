package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;

@Entity
public class IncludedProduct extends AplosBean {
	private static final long serialVersionUID = 496665816880028974L;

	@Any( metaColumn = @Column( name = "realizedProductRetriever_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn( name = "realizedProductRetriever_id" )
	@DynamicMetaValues
	private RealizedProductRetriever realizedProductRetriever;
	private int quantity;
	private boolean isSerialNumberRequired;

	public IncludedProduct getCopy() {
		IncludedProduct includedProduct = new IncludedProduct();
		includedProduct.setRealizedProductRetriever(getRealizedProductRetriever());
		includedProduct.setQuantity(getQuantity());
		includedProduct.setSerialNumberRequired(isSerialNumberRequired());
		return includedProduct;
	}

	@Override
	public String getDisplayName() {
		RealizedProduct realizedProduct = getRealizedProductRetriever().retrieveRealizedProduct( null );
		if (realizedProduct == null) {
			return "No Retrieveable Product";
		} else {
			return realizedProduct.getDisplayName();
		}
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setSerialNumberRequired(boolean isSerialNumberRequired) {
		this.isSerialNumberRequired = isSerialNumberRequired;
	}
	public boolean isSerialNumberRequired() {
		return isSerialNumberRequired;
	}

	public void setRealizedProductRetriever(RealizedProductRetriever realizedProductRetriever) {
		this.realizedProductRetriever = realizedProductRetriever;
	}

	public RealizedProductRetriever getRealizedProductRetriever() {
		return realizedProductRetriever;
	}
}
