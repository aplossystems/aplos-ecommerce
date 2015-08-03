package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.FormatUtil;

@Entity
@PluralDisplayName(name="shipping bands")
public class ShippingBand extends AplosBean {

	private static final long serialVersionUID = -2286113739326167160L;
	private String name;
	private BigDecimal charge;
	private BigDecimal upperLimit;
	private BigDecimal lowerLimit;

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

	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}

	public BigDecimal getCharge() {
		return charge;
	}

	public String getChargeString() {
		return FormatUtil.formatCurrentCurrency(charge);
	}

	public String getLimitString() {
		if (upperLimit != null) {
			if (lowerLimit != null) {
				return lowerLimit + " - " + upperLimit;
			} else {
				return "0.00 - " + upperLimit;
			}
		} else if (lowerLimit != null) {
			return lowerLimit + "+";
		} else {
			return "Error!";
		}
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

}
