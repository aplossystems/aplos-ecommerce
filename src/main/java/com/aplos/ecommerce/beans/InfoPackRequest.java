package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;


@Entity
public class InfoPackRequest extends AplosAbstractBean implements BulkEmailSource {
	private static final long serialVersionUID = 2883140271086705043L;
	
	private String firstName;
	private String surname;
	private String emailAddress;
	@ManyToOne
	private NewsEntry newsEntry;

	@Override
	public String getSourceUniqueDisplayName() {
		return CommonUtil.getStringOrEmpty(getFullName()) + " (" + CommonUtil.getStringOrEmpty(getEmailAddress()) + ")";
	}
	
	public String getFullName() {
		return FormatUtil.getFullName(getFirstName(), getSurname());
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public NewsEntry getNewsEntry() {
		return newsEntry;
	}

	public void setNewsEntry(NewsEntry newsEntry) {
		this.newsEntry = newsEntry;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
