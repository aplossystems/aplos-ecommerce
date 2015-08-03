package com.aplos.ecommerce.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.MailRecipientFinder;

@ManagedBean
@SessionScoped
@Entity
public class CustomerType extends AplosBean {
	private static final long serialVersionUID = 1308436302217148136L;

	private String name;
	private String description;

	@ManyToOne
	private MailRecipientFinder mailRecipientFinder;

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
}
