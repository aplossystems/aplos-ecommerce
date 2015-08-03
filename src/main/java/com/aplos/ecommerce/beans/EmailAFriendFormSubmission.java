package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.utils.CommonUtil;


@Entity
public class EmailAFriendFormSubmission extends AplosBean implements BulkEmailSource {
	private static final long serialVersionUID = 1905471953940576073L;
	
	private String message="...and thought of you";
	private String sender="";
	private String recipientEmail="";

	@Override
	public String getSourceUniqueDisplayName() {
		return CommonUtil.getStringOrEmpty(sender +  "'s Friend") + " (" + CommonUtil.getStringOrEmpty(recipientEmail) + ")";
	}
	
	@Override
	public Long getMessageSourceId() {
		return null;
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}
	
	@Override
	public String getFirstName() {
		return sender;
	}

	@Override
	public String getSurname() {
		return "";
	}

	@Override
	public String getEmailAddress() {
		return recipientEmail;
	}
}
