package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@Entity
@DynamicMetaValueKey(oldKey="CUSTOM_AXIS_LABEL")
public class CustomSizeChartAxisLabel extends AplosBean implements PositionedBean, SizeChartAxisLabel {
	private static final long serialVersionUID = 7975916253869584388L;
	
	private Integer positionIdx;
	private String name;
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}
	
	@Override
	public String getSizeChartAxisLabelName() {
		return getName();
	}
	
	@Override
	public int getSizeChartAxisLabelPositionIdx() {
		return getPositionIdx();
	}
	
	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}
	
	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
