package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EmailAFriendFormSubmission;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;


@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class EmailAFriendFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -7361410245593204985L;

	private String feedback="";
	private EmailAFriendFormSubmission emailAFriendFormSubmission;

	public EmailAFriendFeDmb() {
		emailAFriendFormSubmission = new EmailAFriendFormSubmission();
	}

	public void setMessage(String message) {
		emailAFriendFormSubmission.setMessage(message);
	}

	public String getMessage() {
		return emailAFriendFormSubmission.getMessage();
	}

	public void setSender(String sender) {
		emailAFriendFormSubmission.setSender(sender);
	}

	public String getSender() {
		return emailAFriendFormSubmission.getSender();
	}

	public void setRecipientEmail(String recipientEmail) {
		emailAFriendFormSubmission.setRecipientEmail(recipientEmail);
	}

	public String getRecipientEmail() {
		return emailAFriendFormSubmission.getRecipientEmail();
	}

	public void sendFriendEmail() {
		JSFUtil.getServletContext().getAttribute(AplosScopedBindings.ACTIVE_USERS);
		if (AplosEmail.sendEmail( EcommerceEmailTemplateEnum.EMAIL_A_FRIEND, emailAFriendFormSubmission).getEmailSentDate() != null) {
			feedback = "Your message has been sent to your friend. Feel free to send the link to any other friend's too.";
		} else {
			feedback = "Could not send E-Mail. Is the address valid?";
		}
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getFeedback() {
		return feedback;
	}

	public boolean getIsValidationRequired() {
		return BackingPage.validationRequired("submitEmailAFriend");
	}
}

