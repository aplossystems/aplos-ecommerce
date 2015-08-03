package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.product.ProductGroup;

@Entity
public class IncludedProductGroup extends AplosBean {
	private static final long serialVersionUID = 106131050216567288L;

	@ManyToOne
	private ProductGroup productGroup;

	private int quantity;
	private boolean isSerialNumberRequired;

	public IncludedProductGroup() {
	}

	public IncludedProductGroup( ProductGroup productGroup, int quantity, boolean isSerialNumberRequired ) {
		setProductGroup( productGroup );
		setQuantity( quantity );
		setSerialNumberRequired(isSerialNumberRequired);
	}

	public IncludedProductGroup getCopy() {
		IncludedProductGroup includedProductGroup = new IncludedProductGroup();
		includedProductGroup.setProductGroup(getProductGroup());
		includedProductGroup.setQuantity(getQuantity());
		includedProductGroup.setSerialNumberRequired(isSerialNumberRequired());
		return includedProductGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
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
}
