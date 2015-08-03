package com.aplos.ecommerce.beans.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.playcom.PlaySizeType;
import com.aplos.ecommerce.enums.AmazonProductType;

@Entity
@ManagedBean
@SessionScoped
@Cache
public class ProductSizeType extends AplosBean {

	private static final long serialVersionUID = -2988065068819513073L;
	private String name;

	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySizeType playSizeType;

	private AmazonProductType amazonProductType;

	public ProductSizeType() {

	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	public PlaySizeType getPlaySizeType() {
		return playSizeType;
	}

	public void setPlaySizeType(PlaySizeType playSizeType) {
		this.playSizeType = playSizeType;
	}

	public AmazonProductType getAmazonProductType() {
		return amazonProductType;
	}

	public void setAmazonProductType(AmazonProductType amazonProductType) {
		this.amazonProductType = amazonProductType;
	}

}













