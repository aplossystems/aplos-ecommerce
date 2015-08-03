package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class FriendReferralEmail extends SourceGeneratedEmailTemplate<FriendReferral> {

	private static final long serialVersionUID = -666595695634861450L;

	public FriendReferralEmail() {
	}
	
	public String getDefaultName() {
		return "Friend Referral";
	}

	@Override
	public String getDefaultSubject() {
		return "{REFERRER_NAME} thinks you're going to be interested in what we have to offer";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "friendReferral.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		FriendReferral friendReferral = new FriendReferral();
		Address address = new Address();
		address.setEmailAddress(adminUser.getEmail());
		address.setContactFirstName(adminUser.getFirstName());
		address.setContactSurname(adminUser.getSurname());
		friendReferral.setAddress(address);
		friendReferral.setReferralBonus(new BigDecimal(5));
		friendReferral.setReferralBonusThreshold(3);
		friendReferral.setReferralLimitPerCalendarMonth(5);
		friendReferral.setReferralPayout(new BigDecimal(15));
		friendReferral.setReferralPayoutPercentage(true);
		friendReferral.setReferreeMinimumSpend(new BigDecimal(25));
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		friendReferral.setReferrer(customer);
		return friendReferral;
	}
	
	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.FRIEND_REFERRAL;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			FriendReferral referral) {
		super.addSubjectJDynamiTeValues(jDynamiTe, referral);
		jDynamiTe.setVariable("REFERRER_NAME", referral.getAddress().getContactFullName());
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			FriendReferral referral) {
		super.addContentJDynamiTeValues(jDynamiTe, referral);
		jDynamiTe.setVariable("REFEREE_NAME", referral.getAddress().getContactFullName() );
		jDynamiTe.setVariable("REFERRER_NAME", referral.getReferrer().getFullName());
		jDynamiTe.setVariable("CODE", referral.getUniqueCode() );
		jDynamiTe.setVariable("ADDRESS_HTML", referral.getAddress().getToHtmlFull() );
		jDynamiTe.setVariable("SERVER_URL", (String) JSFUtil.getServletContext().getAttribute(AplosScopedBindings.SERVER_URL) );
	}
}
