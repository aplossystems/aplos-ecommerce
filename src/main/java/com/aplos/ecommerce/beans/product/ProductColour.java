package com.aplos.ecommerce.beans.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.playcom.PlayColour;
import com.aplos.ecommerce.enums.AmazonColour;

@Entity
@ManagedBean
@SessionScoped
@Cache
public class ProductColour extends AplosBean implements Comparable<ProductColour> {
	private static final long serialVersionUID = -4217303238287947368L;
	private String name;

	@ManyToOne(fetch=FetchType.LAZY)
	private PlayColour playColour;

	private AmazonColour amazonColour;

	public ProductColour() {
		name="";
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

	@Override
	public int compareTo(ProductColour o) {
		return this.name.compareTo(o.getName());
	}

	public PlayColour getPlayColour() {
		return playColour;
	}

	public void setPlayColour(PlayColour playColour) {
		this.playColour = playColour;
	}

	public AmazonColour getAmazonColour() {
		return amazonColour;
	}

	public void setAmazonColour(AmazonColour amazonColour) {
		this.amazonColour = amazonColour;
	}

}
