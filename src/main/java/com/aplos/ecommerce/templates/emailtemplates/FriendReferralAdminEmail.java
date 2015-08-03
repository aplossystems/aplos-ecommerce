package com.aplos.ecommerce.templates.emailtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.communication.SingleEmailRecord;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class FriendReferralAdminEmail extends FriendReferralEmail {

	private static final long serialVersionUID = -666595695634861450L;

	public FriendReferralAdminEmail() {
	}
	
	public String getDefaultName() {
		return "Friend Referral Admin";
	}

	@Override
	public String getDefaultSubject() {
		return "Referral by {REFERRER_NAME}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "friendReferral.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.FRIEND_REFER_ADMIN;
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			FriendReferral bulkEmailRecipient, FriendReferral emailGenerator, SingleEmailRecord singleEmailRecord ) {
		super.addContentJDynamiTeValues(jDynamiTe, bulkEmailRecipient, emailGenerator, singleEmailRecord);
		jDynamiTe.setVariable("ADMIN_NAME", CommonUtil.getAdminUser().getFullName() + " (admin)" );
	}
}

