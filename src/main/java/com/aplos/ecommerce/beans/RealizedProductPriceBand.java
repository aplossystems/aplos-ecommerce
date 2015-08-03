package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.ApplicationUtil;

@Entity
public class RealizedProductPriceBand extends AplosBean {

	private static final long serialVersionUID = 5399930553218184024L;

	@ManyToOne
	private RealizedProductBand realizedProductBand;
	private BigDecimal price;
	
	public RealizedProductPriceBand() { 
	}
	
	public RealizedProductPriceBand getCopy() {
		try {
			return (RealizedProductPriceBand) clone();
		} catch( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError(cnsEx);
		}
		return null;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public RealizedProductBand getRealizedProductBand() {
		return realizedProductBand;
	}

	public void setRealizedProductBand(RealizedProductBand realizedProductBand) {
		this.realizedProductBand = realizedProductBand;
	}

	@Override
	public String getDisplayName() {
		if (realizedProductBand == null) {
			return "Price Band";
		}
		return realizedProductBand.getDisplayName();
	}
}

