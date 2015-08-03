package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
@PluralDisplayName(name="email friends")
public class EmailAFriend extends AplosBean {

	private static final long serialVersionUID = -7992691952715482383L;

	private String senderName,senderEmail,receiverEmail,receiverName;
	@Column(columnDefinition="LONGTEXT")
	private String comment;

	public EmailAFriend() {
		comment="";
		senderName="";
		senderEmail="";
		receiverEmail="";
		receiverName="";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String newComment) {
		this.comment = newComment;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String newSenderName) {
		this.senderName = newSenderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String newSenderEmail) {
		this.senderEmail = newSenderEmail;
	}

	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String newReceiverEmail) {
		this.receiverEmail = newReceiverEmail;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String newReceiverName) {
		this.receiverName = newReceiverName;
	}
}
