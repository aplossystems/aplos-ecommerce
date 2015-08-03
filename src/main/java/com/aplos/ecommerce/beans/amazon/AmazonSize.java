package com.aplos.ecommerce.beans.amazon;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.ecommerce.enums.AmazonProductType;

@Entity
public class AmazonSize extends AplosBean implements PositionedBean {
	private static final long serialVersionUID = -4441207988955613796L;
	
	private AmazonProductType amazonProductType;
	private String name;
	private Integer positionIdx;
	
	@Override
	public String getDisplayName() {
		return getName();
	}
	
	public AmazonProductType getAmazonProductType() {
		return amazonProductType;
	}
	public void setAmazonProductType(AmazonProductType amazonProductType) {
		this.amazonProductType = amazonProductType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}
}
