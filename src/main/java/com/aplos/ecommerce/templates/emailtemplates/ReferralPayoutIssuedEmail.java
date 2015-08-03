package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.beans.StoreCreditVoucher;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class ReferralPayoutIssuedEmail extends SourceGeneratedEmailTemplate<FriendReferral> {

	private static final long serialVersionUID = 3748522051558630894L;

	public ReferralPayoutIssuedEmail() {
	}
	
	public String getDefaultName() {
		return "Referral Payout Issued";
	}

	@Override
	public String getDefaultSubject() {
		return "Notification of {COMPANY_NAME} store credit for {REFEREE_NAME} referral";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "referralPayoutIssued.html" );
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
		StoreCreditVoucher referralVoucher = new StoreCreditVoucher(friendReferral.getReferralPayout(), friendReferral.getReferrer(), "referral of " + friendReferral.getReferee().getFullName());
		referralVoucher.generateVoucherCode();
		referralVoucher.setUsed(true);
		List<StoreCreditVoucher> vouchersIssued = new ArrayList<StoreCreditVoucher>();
		vouchersIssued.add(referralVoucher);
		friendReferral.setCreditVouchersAwarded(vouchersIssued);
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		friendReferral.setReferrer(customer);
		return friendReferral;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.REFERRAL_PAYOUT_ISSUED;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			FriendReferral friendReferral) {
		super.addSubjectJDynamiTeValues(jDynamiTe, friendReferral);
		jDynamiTe.setVariable( "REFEREE_NAME", friendReferral.getReferee().getFullName() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			FriendReferral friendReferral) {
		super.addContentJDynamiTeValues(jDynamiTe, friendReferral);
		jDynamiTe.setVariable( "REFERRER_NAME", friendReferral.getReferrer().getFullName() );
		jDynamiTe.setVariable( "REFEREE_NAME", friendReferral.getReferee().getFullName() );
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		jDynamiTe.setVariable( "COMPANY_NAME", companyName );
		for(StoreCreditVoucher creditVoucher : friendReferral.getCreditVouchersAwarded()) {
			jDynamiTe.setVariable( "VOUCHER_VALUE_REPEATED", creditVoucher.getCartItemName());
			jDynamiTe.parseDynElem( "voucherList" );
		}
	}
}
