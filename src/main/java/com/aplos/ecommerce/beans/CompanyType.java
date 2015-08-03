package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.MailRecipientFinder;

@ManagedBean
@SessionScoped
@Entity
public class CompanyType extends AplosBean {
	private static final long serialVersionUID = 1308436302217148136L;

	private String name;
	private String description;

	@ManyToOne
	private DiscountAllowance discountAllowance;

	@ManyToOne
	private MailRecipientFinder mailRecipientFinder;

	public BigDecimal getDiscountPercentage() {
		if( getDiscountAllowance() == null ) {
			return new BigDecimal( 0 );
		} else {
			return getDiscountAllowance().getDiscountPercentage();
		}
	}

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

	public void setMailRecipientFinder(MailRecipientFinder mailRecipientFinder) {
		this.mailRecipientFinder = mailRecipientFinder;
	}

	public MailRecipientFinder getMailRecipientFinder() {
		return mailRecipientFinder;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDiscountAllowance(DiscountAllowance discountAllowance) {
		this.discountAllowance = discountAllowance;
	}

	public DiscountAllowance getDiscountAllowance() {
		return discountAllowance;
	}


}
