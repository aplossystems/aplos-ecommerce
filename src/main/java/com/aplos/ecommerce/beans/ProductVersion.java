package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.product.Product;

@Entity
public class ProductVersion extends AplosBean {
	private static final long serialVersionUID = 5436441922062107252L;

	@ManyToOne
	private Product product;
	private int versionMajor;
	private int versionMinor;
	private int versionPatch;
	@Column(columnDefinition="LONGTEXT")
	private String details;

	@Override
	public String getDisplayName() {
		return "v" + getVersionMajor() + "." + getVersionMinor();
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	public void setVersionMajor(int versionMajor) {
		this.versionMajor = versionMajor;
	}

	public int getVersionMajor() {
		return versionMajor;
	}

	public void setVersionMinor(int versionMinor) {
		this.versionMinor = versionMinor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	public void setVersionPatch(int versionPatch) {
		this.versionPatch = versionPatch;
	}

	public int getVersionPatch() {
		return versionPatch;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDetails() {
		return details;
	}
}
