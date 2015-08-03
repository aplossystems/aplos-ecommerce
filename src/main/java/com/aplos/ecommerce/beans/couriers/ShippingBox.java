package com.aplos.ecommerce.beans.couriers;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
@PluralDisplayName(name="shipping boxes")
public class ShippingBox extends AplosBean {
	private static final long serialVersionUID = -8583310718315687312L;

	private String name;
	private int width;
	private int depth;
	private int height;

	@Override
	public String getDisplayName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int calculateVolume() {
		return width * depth * height;
	}

	public String getName() {
		return name;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getDepth() {
		return depth;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
}
