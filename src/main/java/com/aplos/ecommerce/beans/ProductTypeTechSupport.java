package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.ecommerce.beans.product.ProductType;

@Entity
public class ProductTypeTechSupport extends AplosBean implements PositionedBean {

	private static final long serialVersionUID = -7203029270270090216L;

	@ManyToOne
	private ProductType productType;
	private Integer positionIdx;

	@Override
	public String getDisplayName() {
		if( getProductType() == null ) {
			return "New Product Type Reviews";
		} else {
			return getProductType().getDisplayName();
		}
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}

}
